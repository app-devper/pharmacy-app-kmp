package app.devper.pharm.presentation.labels

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LabelPrint

fun NavGraphBuilder.labelPrintNav() {
    composable<LabelPrint> {
        LabelPrintScreen()
    }
}
