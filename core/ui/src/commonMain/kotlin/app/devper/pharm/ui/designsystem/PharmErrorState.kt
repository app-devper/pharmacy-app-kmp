package app.devper.pharm.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.i18n.pharmStrings

fun <T : AppException> T?.unlessPageShowsError(pageShowsError: Boolean): T? =
    if (pageShowsError) null else this

@Composable
fun PharmErrorState(
    modifier: Modifier = Modifier,
    title: String = pharmStrings.commonLoadFailed,
    subtitle: String? = pharmStrings.commonLoadFailedHint,
    onRetry: (() -> Unit)? = null,
) {
    PharmEmptyState(
        title = title,
        subtitle = subtitle,
        icon = PharmIcons.Warning,
        modifier = modifier,
        action = onRetry?.let {
            {
                PharmButton(
                    label = pharmStrings.commonRetry,
                    onClick = it,
                    size = PharmButtonSize.Sm,
                )
            }
        },
    )
}
