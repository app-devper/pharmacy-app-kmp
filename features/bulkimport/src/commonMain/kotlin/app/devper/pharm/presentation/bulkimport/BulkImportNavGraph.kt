package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.bulkImportGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<BulkImport> {
        ShelledScreen(
            title = "นำเข้ายาด้วย JSON",
            currentRoute = BulkImport::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            BulkImportScreen()
        }
    }
}
