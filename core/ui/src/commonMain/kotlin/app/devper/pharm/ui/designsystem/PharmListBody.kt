package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.common.AppException

enum class ListBody { Loading, Failed, Empty, Content }

fun listBodyOf(loading: Boolean, error: AppException?, isEmpty: Boolean): ListBody = when {
    !isEmpty -> ListBody.Content
    loading -> ListBody.Loading
    error != null -> ListBody.Failed
    else -> ListBody.Empty
}

@Composable
fun PharmListBody(
    loading: Boolean,
    error: AppException?,
    isEmpty: Boolean,
    onRetry: (() -> Unit)?,
    empty: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    when (listBodyOf(loading, error, isEmpty)) {
        ListBody.Loading -> PharmListSkeleton(modifier = Modifier.fillMaxSize())
        ListBody.Failed -> PharmErrorState(onRetry = onRetry)
        ListBody.Empty -> empty()
        ListBody.Content -> content()
    }
}
