package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun PharmDarkPreview(content: @Composable () -> Unit) {
    PharmacyTheme(darkTheme = true) {
        Box(modifier = Modifier.background(pharmTokens.colors.bgPage)) {
            content()
        }
    }
}

@Composable
internal fun PharmLightPreview(content: @Composable () -> Unit) {
    PharmacyTheme(darkTheme = false) {
        Box(modifier = Modifier.background(pharmTokens.colors.bgPage)) {
            content()
        }
    }
}
