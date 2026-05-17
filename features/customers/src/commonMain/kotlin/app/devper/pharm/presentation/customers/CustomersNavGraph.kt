package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.customersGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<Customers> {
        ShelledScreen(
            title = "ลูกค้า",
            currentRoute = Customers::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            CustomersScreen(
                onAddCustomer = { navController.navigate(CustomerAdd) { launchSingleTop = true } },
                onOpenCustomer = { id -> navController.navigate(CustomerDetail(id)) { launchSingleTop = true } },
                onEditCustomer = { id -> navController.navigate(CustomerEdit(id)) { launchSingleTop = true } },
            )
        }
    }
    composable<CustomerAdd> {
        CustomerFormScreen(
            customerId = null,
            onBack = { navController.popBackStack() },
        )
    }
    composable<CustomerEdit> { entry ->
        val route = entry.toRoute<CustomerEdit>()
        CustomerFormScreen(
            customerId = route.id,
            onBack = { navController.popBackStack() },
        )
    }
    composable<CustomerDetail> { entry ->
        val route = entry.toRoute<CustomerDetail>()
        CustomerDetailScreen(
            customerId = route.id,
            onBack = { navController.popBackStack() },
            onEdit = { editId -> navController.navigate(CustomerEdit(editId)) { launchSingleTop = true } },
        )
    }
}
