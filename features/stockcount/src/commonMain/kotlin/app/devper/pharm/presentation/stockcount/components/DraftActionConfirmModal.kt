package app.devper.pharm.presentation.stockcount.components

import androidx.compose.runtime.Composable
import app.devper.pharm.presentation.stockcount.StockCountDraftAction
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun DraftActionConfirmModal(
    action: StockCountDraftAction?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val s = pharmStrings
    val title = when (action) {
        StockCountDraftAction.FillFromSystem -> s.stockCountFormFillConfirmTitle
        StockCountDraftAction.ClearDraft -> s.stockCountFormClearConfirmTitle
        null -> ""
    }
    val message = when (action) {
        StockCountDraftAction.FillFromSystem -> s.stockCountFormFillConfirmMessage
        StockCountDraftAction.ClearDraft -> s.stockCountFormClearConfirmMessage
        null -> ""
    }
    val confirmLabel = when (action) {
        StockCountDraftAction.FillFromSystem -> s.stockCountFormFillConfirmCta
        StockCountDraftAction.ClearDraft -> s.stockCountFormClearConfirmCta
        null -> ""
    }
    val variant = when (action) {
        StockCountDraftAction.FillFromSystem -> PharmButtonVariant.Primary
        StockCountDraftAction.ClearDraft -> PharmButtonVariant.Danger
        null -> PharmButtonVariant.Primary
    }

    PharmModal(
        open = action != null,
        onDismiss = onCancel,
        title = title,
        subtitle = message,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onCancel,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Md,
            )
            PharmButton(
                label = confirmLabel,
                onClick = onConfirm,
                variant = variant,
                size = PharmButtonSize.Md,
            )
        },
    ) {}
}
