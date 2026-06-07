package app.devper.pharm.presentation.reports

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun EodCloseButton(
    closed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    if (closed) {
        PharmButton(
            label = s.reportsEodClosedBadge,
            onClick = {},
            enabled = false,
            variant = PharmButtonVariant.Secondary,
            size = PharmButtonSize.Md,
            modifier = modifier,
            leadingIcon = {
                Icon(
                    imageVector = PharmIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
        )
    } else {
        PharmButton(
            label = s.reportsTabEod,
            onClick = onClick,
            enabled = enabled,
            variant = PharmButtonVariant.Primary,
            size = PharmButtonSize.Md,
            modifier = modifier,
        )
    }
}

@Composable
internal fun EodConfirmCloseModal(
    open: Boolean,
    report: EodReport?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val s = pharmStrings
    PharmModal(
        open = open,
        onDismiss = onCancel,
        title = s.reportsEodTitle,
        subtitle = s.reportsEodConfirmMessage,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onCancel,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Md,
            )
            PharmButton(
                label = s.reportsEodConfirmTitle,
                onClick = onConfirm,
                variant = PharmButtonVariant.Primary,
                size = PharmButtonSize.Md,
            )
        },
    ) {
        val t = pharmTokens
        if (report != null) {
            Text(
                text = "${s.reportsEodDate} ${app.devper.pharm.ui.format.localDateToBuddhist(report.date).ifBlank { s.reportsEodToday }}",
                style = PharmText.meta.copy(color = t.colors.fgMuted),
            )
            Text(
                text = s.reportsEodNetSalesLine(fmtBaht(report.totalSales), report.billCount),
                style = PharmText.body,
            )
            Text(
                text = s.reportsEodCashLine(fmtBaht(report.netCash)),
                style = PharmText.bodySm.copy(color = t.colors.fg2),
            )
        } else {
            Text(text = s.reportsEmptyDay, style = PharmText.body)
        }
    }
}
