package app.devper.pharm

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.presentation.navigation.AppNavHost
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    PharmacyTheme {
        Surface {
            AppNavHost()
        }
    }
}
