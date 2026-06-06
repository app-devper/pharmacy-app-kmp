package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.kyGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<Ky9> {
        ShelledScreen(
            title = KyFormType.Ky9.label,
            currentRoute = Ky9::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            Ky9Screen(
                onSwitchForm = { switchKyForm(it, navController) },
                onAddEntry = { navController.navigate(Ky9Add) { launchSingleTop = true } },
            )
        }
    }
    composable<Ky9Add> {
        Ky9AddScreen(onBack = { navController.popBackStack() })
    }
    composable<Ky10> {
        ShelledScreen(
            title = KyFormType.Ky10.label,
            currentRoute = Ky10::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            KyListScreen(formType = KyFormType.Ky10, onSwitchForm = { switchKyForm(it, navController) })
        }
    }
    composable<Ky11> {
        ShelledScreen(
            title = KyFormType.Ky11.label,
            currentRoute = Ky11::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            KyListScreen(formType = KyFormType.Ky11, onSwitchForm = { switchKyForm(it, navController) })
        }
    }
    composable<Ky12> {
        ShelledScreen(
            title = KyFormType.Ky12.label,
            currentRoute = Ky12::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            KyListScreen(formType = KyFormType.Ky12, onSwitchForm = { switchKyForm(it, navController) })
        }
    }
}

private fun switchKyForm(form: KyFormType, navController: NavController) {
    val dest: Any = when (form) {
        KyFormType.Ky9  -> Ky9
        KyFormType.Ky10 -> Ky10
        KyFormType.Ky11 -> Ky11
        KyFormType.Ky12 -> Ky12
    }
    navController.navigate(dest) {
        launchSingleTop = true
    }
}
