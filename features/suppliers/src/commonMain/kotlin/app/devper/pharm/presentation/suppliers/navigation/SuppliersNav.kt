package app.devper.pharm.presentation.suppliers

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object Suppliers

@Serializable
data object SupplierAdd

@Serializable
data class SupplierEdit(val id: String)

fun NavGraphBuilder.suppliersNav(navController: NavController) {
    composable<Suppliers> {
        SuppliersScreen(
            onAddSupplier = { navController.navigate(SupplierAdd) { launchSingleTop = true } },
            onEditSupplier = { id -> navController.navigate(SupplierEdit(id)) { launchSingleTop = true } },
        )
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
