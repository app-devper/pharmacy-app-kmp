package app.devper.pharm.presentation.customers

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object Customers

@Serializable
data object CustomerAdd

@Serializable
data class CustomerEdit(val id: String)

@Serializable
data class CustomerDetail(val id: String)

fun NavGraphBuilder.customersNav(navController: NavController) {
    composable<Customers> {
        CustomersScreen(
            onAddCustomer = { navController.navigate(CustomerAdd) { launchSingleTop = true } },
            onOpenCustomer = { id -> navController.navigate(CustomerDetail(id)) { launchSingleTop = true } },
            onEditCustomer = { id -> navController.navigate(CustomerEdit(id)) { launchSingleTop = true } },
        )
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
