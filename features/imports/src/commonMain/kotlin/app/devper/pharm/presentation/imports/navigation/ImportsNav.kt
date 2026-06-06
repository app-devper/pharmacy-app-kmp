package app.devper.pharm.presentation.imports

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object Imports

@Serializable
data object ImportNew

@Serializable
data class ImportEdit(val id: String)

@Serializable
data class ImportDetail(val id: String)

fun NavGraphBuilder.importsNav(navController: NavController) {
    composable<Imports> {
        ImportsScreen(
            onAddImport = { navController.navigate(ImportNew) { launchSingleTop = true } },
            onOpenImport = { id -> navController.navigate(ImportDetail(id)) { launchSingleTop = true } },
            onEditImport = { id -> navController.navigate(ImportEdit(id)) { launchSingleTop = true } },
        )
    }
    composable<ImportNew> {
        ImportFormScreen(
            importId = null,
            onBack = { navController.popBackStack() },
        )
    }
    composable<ImportEdit> { entry ->
        val route = entry.toRoute<ImportEdit>()
        ImportFormScreen(
            importId = route.id,
            onBack = { navController.popBackStack() },
        )
    }
    composable<ImportDetail> { entry ->
        val route = entry.toRoute<ImportDetail>()
        ImportDetailScreen(
            importId = route.id,
            onBack = { navController.popBackStack() },
            onEdit = { editId -> navController.navigate(ImportEdit(editId)) { launchSingleTop = true } },
        )
    }
}
