package app.devper.pharm.presentation.users

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsersListScreen(
    onAddUser: () -> Unit = {},
    onEditUser: (UmUser) -> Unit = {},
    viewModel: UsersListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReloadOnResume(viewModel::reload)
    UsersListContent(
        state = state,
        callbacks = UsersListCallbacks(
            onSearch = viewModel::setSearch,
            onAddUser = onAddUser,
            onEditUser = onEditUser,
            onRequestDelete = viewModel::requestDelete,
            onConfirmDelete = viewModel::confirmDelete,
            onRequestRoleEdit = viewModel::requestRoleEdit,
            onSubmitRoleChange = viewModel::submitRoleChange,
            onRequestStatusToggle = viewModel::requestStatusToggle,
            onConfirmStatusToggle = viewModel::confirmStatusToggle,
            onRequestPasswordSet = viewModel::requestPasswordSet,
            onSubmitPasswordSet = viewModel::submitPasswordSet,
            onDismissAction = viewModel::dismissAction,
            onDismissError = viewModel::dismissError,
        ),
    )
}
