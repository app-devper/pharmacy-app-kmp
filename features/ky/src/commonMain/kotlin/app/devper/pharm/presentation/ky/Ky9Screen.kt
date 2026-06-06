package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.model.KyFormType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky9Screen(
    onSwitchForm: (KyFormType) -> Unit = {},
    onAddEntry: () -> Unit = {},
    viewModel: Ky9ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.reload()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Ky9Content(
        state = state,
        callbacks = Ky9Callbacks(
            onSwitchForm = onSwitchForm,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            onAddEntry = onAddEntry,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
