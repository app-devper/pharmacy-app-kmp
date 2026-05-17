package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.suppliersGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<Suppliers> {
        ShelledScreen(
            title = "ผู้จัดจำหน่าย",
            currentRoute = Suppliers::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            SuppliersScreen(
                onAddSupplier = { navController.navigate(SupplierAdd) { launchSingleTop = true } },
                onEditSupplier = { id -> navController.navigate(SupplierEdit(id)) { launchSingleTop = true } },
            )
        }
    }
    composable<SupplierAdd> {
        SupplierFormScreen(
            supplierId = null,
            onBack = { navController.popBackStack() },
        )
    }
    composable<SupplierEdit> { entry ->
        val route = entry.toRoute<SupplierEdit>()
        SupplierFormScreen(
            supplierId = route.id,
            onBack = { navController.popBackStack() },
        )
    }
}
