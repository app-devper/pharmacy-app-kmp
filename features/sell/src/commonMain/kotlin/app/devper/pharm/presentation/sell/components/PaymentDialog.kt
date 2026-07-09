package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.devper.pharm.presentation.sell.cashQuickAmounts
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmKeypad
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
fun PaymentDialog(
    received: String,
    total: Double,
    checkingOut: Boolean,
    onReceivedChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSubmitExact: () -> Unit,
    onDismiss: () -> Unit,
) {
    val s = pharmStrings
    val receivedNum = received.toDoubleOrNull() ?: 0.0
    val change = receivedNum - total
    val isExactOrEmpty = receivedNum == 0.0
    val isShort = receivedNum > 0.0 && change < 0.0

    val onKeypadKey = { key: String ->
        val hasDot = received.contains(".")
        val decimalsFull = hasDot && received.substringAfter('.').length >= 2
        if (!(key == "." && hasDot) && !decimalsFull) {
            val base = if (received == "0" && key != ".") "" else received
            onReceivedChange((base + key).filter { c -> c.isDigit() || c == '.' })
        }
    }
    val onKeypadBackspace = { onReceivedChange(received.dropLast(1)) }

    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = s.sellPayDialogTitle,
        size = PharmModalSize.Sm,
        dismissOnBackPress = !checkingOut,
        dismissOnClickOutside = false,
        footer = {
            PaymentCta(
                isExactOrEmpty = isExactOrEmpty,
                isShort = isShort,
                total = total,
                change = change,
                checkingOut = checkingOut,
                onSubmit = onSubmit,
                onSubmitExact = onSubmitExact,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.sellPayCashLabel,
                        style = PharmText.micro.copy(color = pharmTokens.colors.fgMuted),
                    )
                    Text(
                        text = fmtBaht(if (isExactOrEmpty) total else receivedNum),
                        style = PharmText.h2.tabular(),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = s.sellPayTotalReceived,
                        style = PharmText.micro.copy(color = pharmTokens.colors.fgMuted),
                    )
                    Text(
                        text = fmtBaht(if (isExactOrEmpty) total else receivedNum),
                        style = if (isExactOrEmpty) {
                            PharmText.h2.copy(color = pharmTokens.colors.fgMuted).tabular()
                        } else {
                            PharmText.h2.tabular()
                        },
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentInfoCard(
                    label = s.sellPayAmountDue,
                    value = fmtBaht(total),
                    bg = pharmTokens.colors.infoBg,
                    fg = pharmTokens.colors.infoFg,
                    modifier = Modifier.weight(1f),
                )
                PaymentInfoCard(
                    label = s.sellPayChangeLabel,
                    value = when {
                        isExactOrEmpty -> s.sellPayExactTender
                        isShort -> s.sellShortBy(fmtBaht(-change))
                        else -> fmtBaht(change)
                    },
                    bg = pharmTokens.colors.warningBg,
                    fg = pharmTokens.colors.warningFg,
                    modifier = Modifier.weight(1f),
                )
            }

            val quickAmounts = cashQuickAmounts(total)
            if (quickAmounts.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickAmounts.forEach { amount ->
                        PharmButton(
                            label = "฿$amount",
                            onClick = { onReceivedChange(amount.toString()) },
                            variant = PharmButtonVariant.Outline,
                            size = PharmButtonSize.Sm,
                            enabled = !checkingOut,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            PharmKeypad(
                onKey = onKeypadKey,
                onBackspace = onKeypadBackspace,
                enabled = !checkingOut,
            )
        }
    }
}

@Composable
private fun PaymentInfoCard(
    label: String,
    value: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .clip(t.shapes.md)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = label, style = PharmText.micro.copy(color = fg))
        val valueStyle = PharmText.displayTotal.copy(color = fg)
        BasicText(
            text = value,
            style = valueStyle,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 14.sp,
                maxFontSize = valueStyle.fontSize,
            ),
        )
    }
}

@Composable
private fun PaymentCta(
    isExactOrEmpty: Boolean,
    isShort: Boolean,
    total: Double,
    change: Double,
    checkingOut: Boolean,
    onSubmit: () -> Unit,
    onSubmitExact: () -> Unit,
) {
    val s = pharmStrings
    val label = when {
        isExactOrEmpty -> s.sellPayExactCta
        isShort -> s.sellShortBy(fmtBaht(-change))
        else -> s.sellPayChangeCta(fmtBaht(change))
    }
    PharmButton(
        label = label,
        onClick = { if (isExactOrEmpty) onSubmitExact() else onSubmit() },
        size = PharmButtonSize.Lg,
        enabled = !checkingOut && !isShort && total > 0.0,
        loading = checkingOut,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
private fun PaymentDialog_Exact_Preview() {
    PharmacyTheme {
        PaymentDialog(
            received = "",
            total = 210.0,
            checkingOut = false,
            onReceivedChange = {},
            onSubmit = {},
            onSubmitExact = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun PaymentDialog_WithChange_Preview() {
    PharmacyTheme {
        PaymentDialog(
            received = "300",
            total = 210.0,
            checkingOut = false,
            onReceivedChange = {},
            onSubmit = {},
            onSubmitExact = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun PaymentDialog_Short_Preview() {
    PharmacyTheme {
        PaymentDialog(
            received = "100",
            total = 210.0,
            checkingOut = false,
            onReceivedChange = {},
            onSubmit = {},
            onSubmitExact = {},
            onDismiss = {},
        )
    }
}
