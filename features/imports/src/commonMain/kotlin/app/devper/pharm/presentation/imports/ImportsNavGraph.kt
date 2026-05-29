package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.importsGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<Imports> {
        ShelledScreen(
            title = "ใบรับสินค้า",
            currentRoute = Imports::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            ImportsScreen(
                onAddImport = { navController.navigate(ImportNew) { launchSingleTop = true } },
                onOpenImport = { id -> navController.navigate(ImportDetail(id)) { launchSingleTop = true } },
                onEditImport = { id -> navController.navigate(ImportEdit(id)) { launchSingleTop = true } },
            )
        }
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
