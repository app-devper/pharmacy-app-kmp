package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.auth.Login
import app.devper.pharm.presentation.auth.authNav
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoggedIn = state.isLoggedIn
    val rootNav = rememberNavController()

    LaunchedEffect(isLoggedIn) {
        rootNav.navigate(if (isLoggedIn) MainRoot else Login) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = rootNav, startDestination = Login) {
        authNav(
            onLoggedIn = {
                rootNav.navigate(MainRoot) {
                    popUpTo(Login) { inclusive = true }
                }
            },
        )
        composable<MainRoot> {
            MainShell(appViewModel = viewModel)
        }
    }
}
