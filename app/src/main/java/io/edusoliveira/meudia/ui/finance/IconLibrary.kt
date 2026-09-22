package io.edusoliveira.meudia.ui.finance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Ícones disponíveis para personalizar uma conta recorrente, além do ícone
 * padrão da categoria. Cada um tem uma chave estável (salva no banco) e um
 * ImageVector para desenhar.
 */
object IconLibrary {
    data class Entry(val key: String, val icon: ImageVector, val label: String)

    val entries: List<Entry> = listOf(
        Entry("home", Icons.Filled.Home, "Casa"),
        Entry("car", Icons.Filled.DirectionsCar, "Carro"),
        Entry("subscription", Icons.Filled.Subscriptions, "Assinatura"),
        Entry("bank", Icons.Filled.AccountBalance, "Banco"),
        Entry("donation", Icons.Filled.VolunteerActivism, "Doação"),
        Entry("school", Icons.Filled.School, "Educação"),
        Entry("card", Icons.Filled.CreditCard, "Cartão"),
        Entry("flight", Icons.Filled.Flight, "Viagem"),
        Entry("food", Icons.Filled.Fastfood, "Comida"),
        Entry("gym", Icons.Filled.FitnessCenter, "Academia"),
        Entry("pet", Icons.Filled.Pets, "Pet"),
        Entry("health", Icons.Filled.LocalHospital, "Saúde"),
        Entry("game", Icons.Filled.SportsEsports, "Jogos"),
        Entry("music", Icons.Filled.MusicNote, "Música"),
        Entry("movie", Icons.Filled.Theaters, "Cinema/Streaming"),
        Entry("wifi", Icons.Filled.Wifi, "Internet"),
        Entry("energy", Icons.Filled.Bolt, "Energia"),
        Entry("shopping", Icons.Filled.ShoppingBag, "Compras"),
        Entry("robot", Icons.Filled.SmartToy, "Tecnologia/IA"),
        Entry("other", Icons.Filled.Category, "Outro"),
    )

    fun byKey(key: String?): ImageVector? = entries.firstOrNull { it.key == key }?.icon
}
