package io.edusoliveira.meudia.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Lado máximo da imagem guardada e decodificada. Ela aparece num círculo de
 *  ~40dp, então não há motivo para manter uma foto de celular em resolução
 *  total: uma foto de 12 MP ocupa ~48 MB na memória e derruba o app quando a
 *  lista rola e a linha entra na tela. */
private const val MAX_IMAGE_SIZE_PX = 512

/** Imagens já decodificadas, para a lista não redecodificar toda vez que a
 *  linha entra e sai da tela. 8 imagens de 512px ≈ 8 MB no pior caso. */
private val bitmapCache = LruCache<String, ImageBitmap>(8)

/**
 * Copia a imagem escolhida pelo usuário (via photo picker) para o
 * armazenamento interno do app, já reduzida. Necessário porque o Uri do photo
 * picker não é garantido continuar acessível depois que o app fecha.
 */
suspend fun copyImageToInternalStorage(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    runCatching {
        // Os bytes do arquivo são baratos (alguns MB); o caro é a imagem
        // descompactada. Por isso lemos os bytes e só então decodificamos,
        // já reduzindo — nunca abrimos a foto em resolução total.
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@runCatching null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight)
        }
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            ?: return@runCatching null

        // O inSampleSize só reduz em potências de 2, então ainda pode sobrar
        // folga; esta passada deixa o lado maior exatamente no limite.
        val scaled = scaleToMaxSide(decoded)

        val dir = File(context.filesDir, "category_icons").apply { mkdirs() }
        val destination = File(dir, "${UUID.randomUUID()}.jpg")
        destination.outputStream().use { output ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        if (scaled !== decoded) decoded.recycle()
        destination.absolutePath
    }.getOrNull()
}

/** Maior potência de 2 que ainda deixa a imagem acima do tamanho de exibição. */
private fun sampleSizeFor(width: Int, height: Int): Int {
    var amostra = 1
    while (maxOf(width, height) / amostra > MAX_IMAGE_SIZE_PX) {
        amostra *= 2
    }
    return amostra
}

private fun scaleToMaxSide(source: Bitmap): Bitmap {
    val maior = maxOf(source.width, source.height)
    if (maior <= MAX_IMAGE_SIZE_PX) return source
    val fator = MAX_IMAGE_SIZE_PX.toFloat() / maior
    return Bitmap.createScaledBitmap(
        source,
        (source.width * fator).toInt().coerceAtLeast(1),
        (source.height * fator).toInt().coerceAtLeast(1),
        true,
    )
}

/**
 * Decodifica o arquivo local já reduzido. Vale também para fotos salvas por
 * versões antigas do app, que foram gravadas em resolução total.
 */
private fun decodeBitmap(path: String): Bitmap? = runCatching {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight)
    }
    BitmapFactory.decodeFile(path, options)
}.getOrNull()

/** Carrega uma imagem local (caminho absoluto) como [ImageBitmap], fora da main thread. */
@Composable
fun rememberBitmapFromPath(path: String?): ImageBitmap? {
    val state by produceState<ImageBitmap?>(initialValue = path?.let { bitmapCache.get(it) }, key1 = path) {
        if (path == null) {
            value = null
            return@produceState
        }
        bitmapCache.get(path)?.let {
            value = it
            return@produceState
        }
        value = withContext(Dispatchers.IO) {
            decodeBitmap(path)?.asImageBitmap()?.also { bitmapCache.put(path, it) }
        }
    }
    return state
}
