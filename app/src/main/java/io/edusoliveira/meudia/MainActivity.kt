package io.edusoliveira.meudia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.edusoliveira.meudia.ui.ViewModelFactory
import io.edusoliveira.meudia.ui.navigation.MeuDiaRoot
import io.edusoliveira.meudia.ui.theme.MeuDiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MeuDiaApp
        val factory = ViewModelFactory(app.repository)

        setContent {
            MeuDiaTheme {
                MeuDiaRoot(factory = factory)
            }
        }
    }
}
