import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gen.maximizemagic.ui.layout.CelesteBg
import com.gen.maximizemagic.ui.layout.TextPrimary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun MainLayout(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            // Reemplazo estable de TopAppBar usando una Surface manual
            Surface(
                shadowElevation = 3.dp, // Sombra ligera
                color = Color.White // Fondo blanco
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // Altura estándar de la barra
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }
            }
        },
        containerColor = CelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}
