package io.edusoliveira.meudia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = NavyDark,
    primaryContainer = Gold,
    onPrimaryContainer = NavyDark,
    secondary = GoldLight,
    onSecondary = NavyDark,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkOnSurface,
    tertiary = Gold,
    background = BgDeep,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkOnSurfaceMuted,
    error = ExpenseTerracotta,
)

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = LightSurface,
    primaryContainer = Navy,
    onPrimaryContainer = LightSurface,
    secondary = Gold,
    onSecondary = LightOnSurface,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = LightOnSurface,
    tertiary = Gold,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightOnSurfaceMuted,
    error = ExpenseTerracotta,
)

@Composable
fun MeuDiaTheme(
    // Tema escuro é o padrão do app; o claro fica disponível para quando
    // um controle de preferência for exposto ao usuário.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
