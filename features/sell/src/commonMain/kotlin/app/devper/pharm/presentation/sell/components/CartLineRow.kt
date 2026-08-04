package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.extension.EXPIRY_WARNING_DAYS
import app.devper.pharm.domain.extension.nextLotDaysLeft
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.ui.format.localDateToBuddhist
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.designsystem.LocalPharmDensity
import app.devper.pharm.ui.designsystem.PharmDensity
import app.devper.pharm.ui.designsystem.PharmIconButton
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.tabular
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CartLineRow(
    line: CartLine,
    onQtyChange: (qty: Int) -> Unit,
    onRemove: () -> Unit,
    onTapForDiscount: () -> Unit,
    modifier: Modifier = Modifier,
    narrow: Boolean = false,
) {
    var showRemoveConfirm by remember { mutableStateOf(false) }
    val rowVerticalPadding = if (LocalPharmDensity.current == PharmDensity.Compact) 6.dp else 8.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pharmClickable(role = Role.Button, onClick = onTapForDiscount)
            .padding(horizontal = 12.dp, vertical = rowVerticalPadding),
    ) {
        if (narrow) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CartLineName(line = line, modifier = Modifier.fillMaxWidth())
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QtyStepper(
                        qty = line.displayQty,
                        onQtyChange = onQtyChange,
                        onRequestRemove = { showRemoveConfirm = true },
                    )
                    Box(modifier = Modifier.weight(1f))
                    CartLinePrice(line = line)
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CartLineName(line = line, modifier = Modifier.weight(1f))
                QtyStepper(
                    qty = line.displayQty,
                    onQtyChange = onQtyChange,
                    onRequestRemove = { showRemoveConfirm = true },
                )
                CartLinePrice(line = line)
            }
        }
    }

    PharmModal(
        open = showRemoveConfirm,
        onDismiss = { showRemoveConfirm = false },
        title = pharmStrings.sellRemoveLineTitle,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonCancel,
                onClick = { showRemoveConfirm = false },
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
                modifier = Modifier.widthIn(min = 96.dp),
            )
            PharmButton(
                label = pharmStrings.commonDelete,
                onClick = {
                    showRemoveConfirm = false
                    onRemove()
                },
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
                modifier = Modifier.widthIn(min = 96.dp),
            )
        },
    ) {
        Text(
            line.drug.name,
            style = PharmText.body,
        )
    }
}

@Composable
private fun CartLineName(line: CartLine, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = line.drug.name,
                style = PharmText.body,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false),
            )
            line.selectedUnit?.let { alt ->
                Text(
                    text = alt.name,
                    style = PharmText.micro,
                    color = t.colors.fg2,
                )
            }
        }
        Text(
            text = "${fmtBaht(line.unitPrice.amount)} / ${line.displayUnit}",
            style = PharmText.micro,
            color = t.colors.fg3,
        )
        val nextLotExpiry = line.drug.nextLotExpiry
        if (nextLotExpiry != null) {
            val today = remember { Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Bangkok")).date }
            val daysLeft = line.drug.nextLotDaysLeft(today)
            if (daysLeft != null && daysLeft <= EXPIRY_WARNING_DAYS) {
                Text(
                    text = pharmStrings.sellLineNextLot(line.drug.nextLotNumber.orEmpty(), localDateToBuddhist(nextLotExpiry)),
                    style = PharmText.micro,
                    color = if (daysLeft < 0) t.colors.dangerFg else t.colors.warningFg,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun CartLinePrice(line: CartLine) {
    val t = pharmTokens
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.widthIn(min = 84.dp),
    ) {
        Text(
            text = fmtBaht(line.lineTotal.amount),
            style = PharmText.body.tabular(),
            fontWeight = FontWeight.Bold,
            color = t.colors.fg1,
            maxLines = 1,
            textAlign = TextAlign.End,
        )
        Text(
            text = priceMetaLabel(line, pharmStrings),
            style = PharmText.micro.tabular(),
            color = if (line.discount.isPositive) t.colors.dangerFg
                    else t.colors.fg2,
            maxLines = 1,
            textAlign = TextAlign.End,
        )
    }
}

private fun priceMetaLabel(line: CartLine, s: PharmStrings): String {
    if (line.discount.isPositive) {
        val saved = line.discount * line.qty
        return "−${fmtBaht(saved.amount)}"
    }
    return when (line.tier) {
        "wholesale" -> s.sellTierWholesale
        "regular"   -> s.sellTierRegular
        else        -> s.sellTierRetail
    }
}

private const val MAX_QTY = 9999

internal sealed interface CartQtyEditResult {
    data class Quantity(val value: Int) : CartQtyEditResult
    data object Remove : CartQtyEditResult
}

internal fun resolveCartQtyEdit(draft: String, currentQty: Int): CartQtyEditResult {
    val parsed = draft.toIntOrNull() ?: currentQty
    return if (parsed <= 0) {
        CartQtyEditResult.Remove
    } else {
        CartQtyEditResult.Quantity(parsed.coerceAtMost(MAX_QTY))
    }
}

@Composable
private fun QtyStepper(
    qty: Int,
    onQtyChange: (Int) -> Unit,
    onRequestRemove: () -> Unit,
) {
    val t = pharmTokens
    val s = pharmStrings
    var editing by remember { mutableStateOf(false) }
    var draft by remember(qty) { mutableStateOf(qty.toString()) }
    val quantityFocus = remember { FocusRequester() }

    LaunchedEffect(editing) {
        if (editing) quantityFocus.requestFocus()
    }

    fun commit() {
        when (val result = resolveCartQtyEdit(draft, qty)) {
            is CartQtyEditResult.Quantity -> {
                if (result.value != qty) onQtyChange(result.value)
                draft = result.value.toString()
                editing = false
            }
            CartQtyEditResult.Remove -> {
                draft = qty.toString()
                editing = false
                onRequestRemove()
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        StepperCircle(
            onClick = { if (qty > 1) onQtyChange(qty - 1) else onRequestRemove() },
            container = t.colors.dangerBg,
            iconTint = t.colors.dangerFg,
            icon = PharmIcons.Minus,
            description = if (qty > 1) s.sellQtyDecrease else s.sellRemoveLineDesc,
            enabled = true,
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(64.dp)
                .height(48.dp)
                .then(
                    if (editing) Modifier
                    else Modifier
                        .pharmClickable(role = Role.Button) { editing = true }
                        .semantics {
                            contentDescription = "${s.commonQty}: $qty"
                        },
                ),
        ) {
            if (editing) {
                PharmTextField(
                    value = draft,
                    onValueChange = { draft = it.filter(Char::isDigit).take(4) },
                    accessibilityLabel = s.commonQty,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    onImeAction = ::commit,
                    onFocusLost = ::commit,
                    focusRequester = quantityFocus,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = "${qty}x",
                    style = PharmText.bodySm.tabular(),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        StepperCircle(
            onClick = { onQtyChange((qty + 1).coerceAtMost(MAX_QTY)) },
            container = t.colors.accent,
            iconTint = t.colors.surface,
            icon = PharmIcons.Plus,
            description = s.sellQtyIncrease,
            enabled = qty < MAX_QTY,
        )
    }
}

@Composable
private fun StepperCircle(
    onClick: () -> Unit,
    container: Color,
    iconTint: Color,
    icon: ImageVector,
    description: String,
    enabled: Boolean,
) {
    val t = pharmTokens
    PharmIconButton(
        contentDescription = description,
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(48.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) container
                    else t.colors.surfaceRaised
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) iconTint
                       else t.colors.fg2,
                modifier = Modifier.size(13.dp),
            )
        }
    }
}

private fun previewDrug(): Drug = Drug(
    id = "drug-1",
    name = "Starmox dry 125 mg",
    genericName = "Amoxicillin",
    type = null,
    strength = "125 mg",
    barcode = null,
    sellPrice = Money(25.0),
    costPrice = Money(12.0),
    stock = Quantity(240),
    minStock = Quantity(20),
    unit = "ชิ้น",
    regNo = null,
)

@Preview
@Composable
private fun CartLineRow_Base_Preview() {
    PharmacyTheme {
        CartLineRow(
            line = CartLine(drug = previewDrug(), qty = 3),
            onQtyChange = {},
            onRemove = {},
            onTapForDiscount = {},
        )
    }
}

@Preview
@Composable
private fun CartLineRow_Discounted_Preview() {
    PharmacyTheme {
        CartLineRow(
            line = CartLine(drug = previewDrug(), qty = 5, discount = Money(3.0)),
            onQtyChange = {},
            onRemove = {},
            onTapForDiscount = {},
        )
    }
}
