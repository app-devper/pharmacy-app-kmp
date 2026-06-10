package app.devper.pharm.presentation.sell.components

import app.devper.pharm.common.value.Money
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings

private enum class Kind { Flat, Percent }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDiscountSheet(
    current: CartDiscount,
    subtotal: Double,
    onApply: (CartDiscount) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val t = pharmTokens
    var kind by remember(current) {
        mutableStateOf(when (current) {
            is CartDiscount.Percent -> Kind.Percent
            else -> Kind.Flat
        })
    }
    var draft by remember(current) {
        mutableStateOf(when (current) {
            is CartDiscount.Flat    -> if (current.amount.isZero) "" else current.amount.amount.toString().trimEnd('0').trimEnd('.')
            is CartDiscount.Percent -> if (current.percent == 0.0) "" else current.percent.toString().trimEnd('0').trimEnd('.')
            CartDiscount.None       -> ""
        })
    }
    val value = draft.toDoubleOrNull() ?: 0.0
    val proposed = when (kind) {
        Kind.Flat -> CartDiscount.Flat(Money(value))
        Kind.Percent -> CartDiscount.Percent(value)
    }
    val applied = proposed.apply(Money(subtotal)).amount
    val net = (subtotal - applied).coerceAtLeast(0.0)
    val invalid = when (kind) {
        Kind.Flat    -> value < 0 || value > subtotal
        Kind.Percent -> value < 0 || value > 100
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(pharmStrings.sellDiscountCart, style = PharmText.h1)

            Row(
                modifier = Modifier
                    .clip(t.shapes.md)
                    .background(t.colors.borderSubtle)
                    .selectableGroup()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                SegItem(pharmStrings.sellDiscountFlatSeg, active = kind == Kind.Flat) { kind = Kind.Flat }
                SegItem("% เปอร์เซ็นต์", active = kind == Kind.Percent) { kind = Kind.Percent }
            }

            FormField(
                label = if (kind == Kind.Percent) pharmStrings.sellDiscountPercentField else pharmStrings.sellDiscountFlatField,
                error = when {
                    invalid && kind == Kind.Percent -> pharmStrings.sellDiscountPercentInvalid
                    invalid                          -> pharmStrings.sellDiscountFlatInvalid(subtotal.toString())
                    else -> null
                },
            ) {
                PharmTextField(
                    value = draft,
                    onValueChange = { draft = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder = if (kind == Kind.Percent) "0–100" else "0",
                    keyboardType = KeyboardType.Decimal,
                    isError = invalid,
                )
            }

            DiscountSummary(pharmStrings.sellSubtotal, fmtBaht(subtotal))
            DiscountSummary(pharmStrings.sellDiscountDeducted, "−${fmtBaht(applied)}", emphasis = true, discount = true)
            DiscountSummary(pharmStrings.sellNetTotal, fmtBaht(net), emphasis = true)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PharmButton(
                    label = pharmStrings.sellDiscountClear,
                    onClick = { onApply(CartDiscount.None) },
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
                PharmButton(
                    label = pharmStrings.commonSave,
                    onClick = { onApply(proposed) },
                    enabled = !invalid,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SegItem(text: String, active: Boolean, onClick: () -> Unit) {
    val t = pharmTokens
    val bg = if (active) t.colors.surface else androidx.compose.ui.graphics.Color.Transparent
    val fg = if (active) t.colors.fg1 else t.colors.fg3
    Box(
        modifier = Modifier
            .clip(t.shapes.sm)
            .background(bg, t.shapes.sm)
            .selectable(selected = active, role = Role.RadioButton, onClick = { if (!active) onClick() })
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = PharmText.badge.copy(color = fg, fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun DiscountSummary(label: String, value: String, emphasis: Boolean = false, discount: Boolean = false) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = PharmText.bodySm.copy(color = t.colors.fg2))
        val color = if (discount) t.colors.discount else if (emphasis) t.colors.accent else t.colors.fg1
        Text(
            value,
            style = PharmText.bodySm.copy(
                color = color,
                fontWeight = if (emphasis) FontWeight.Bold else FontWeight.Normal,
                fontFeatureSettings = "tnum",
            ),
        )
    }
}
