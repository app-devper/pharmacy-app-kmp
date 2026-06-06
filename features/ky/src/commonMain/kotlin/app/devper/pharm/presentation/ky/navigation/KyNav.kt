package app.devper.pharm.presentation.ky

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.domain.model.KyFormType
import kotlinx.serialization.Serializable

@Serializable
data object Ky9

@Serializable
data object Ky9Add

@Serializable
data object Ky10

@Serializable
data object Ky10Add

@Serializable
data object Ky11

@Serializable
data object Ky11Add

@Serializable
data object Ky12

@Serializable
data object Ky12Add

fun NavGraphBuilder.kyNav(navController: NavController) {
    composable<Ky9> {
        Ky9Screen(
            onSwitchForm = { switchKyForm(it, navController) },
            onAddEntry = { navController.navigate(Ky9Add) { launchSingleTop = true } },
        )
    }
    composable<Ky9Add> {
        Ky9AddScreen(onBack = { navController.popBackStack() })
    }
    composable<Ky10> {
        KyListScreen(
            formType = KyFormType.Ky10,
            onSwitchForm = { switchKyForm(it, navController) },
            onAddEntry = { navController.navigate(Ky10Add) { launchSingleTop = true } },
        )
    }
    composable<Ky10Add> {
        Ky10AddScreen(onBack = { navController.popBackStack() })
    }
    composable<Ky11> {
        KyListScreen(
            formType = KyFormType.Ky11,
            onSwitchForm = { switchKyForm(it, navController) },
            onAddEntry = { navController.navigate(Ky11Add) { launchSingleTop = true } },
        )
    }
    composable<Ky11Add> {
        Ky11AddScreen(onBack = { navController.popBackStack() })
    }
    composable<Ky12> {
        KyListScreen(
            formType = KyFormType.Ky12,
            onSwitchForm = { switchKyForm(it, navController) },
            onAddEntry = { navController.navigate(Ky12Add) { launchSingleTop = true } },
        )
    }
    composable<Ky12Add> {
        Ky12AddScreen(onBack = { navController.popBackStack() })
    }
}

private fun switchKyForm(form: KyFormType, navController: NavController) {
    val dest: Any = when (form) {
        KyFormType.Ky9 -> Ky9
        KyFormType.Ky10 -> Ky10
        KyFormType.Ky11 -> Ky11
        KyFormType.Ky12 -> Ky12
    }
    navController.navigate(dest) {
        launchSingleTop = true
    }
}
