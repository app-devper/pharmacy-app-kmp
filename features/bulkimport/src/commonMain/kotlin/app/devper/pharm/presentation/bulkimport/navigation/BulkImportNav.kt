package app.devper.pharm.presentation.bulkimport

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object BulkImport

fun NavGraphBuilder.bulkImportNav() {
    composable<BulkImport> {
        BulkImportScreen()
    }
}
