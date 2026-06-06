package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.designsystem.LocalPharmDensity
import app.devper.pharm.ui.designsystem.PharmDensity
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CartLineRow(
    line: CartLine,
    onQtyChange: (qty: Int) -> Unit,
    onRemove: () -> Unit,
    onTapForDiscount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showRemoveConfirm by remember { mutableStateOf(false) }
    val rowVerticalPadding = if (LocalPharmDensity.current == PharmDensity.Compact) 8.dp else 14.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTapForDiscount)
            .padding(horizontal = 16.dp, vertical = rowVerticalPadding),
    ) {
        if (maxWidth < 360.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CartLineInfoIcon()
                    CartLineName(line = line, modifier = Modifier.weight(1f))
                    CartLineRemoveButton(onClick = { showRemoveConfirm = true })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QtyStepper(qty = line.displayQty, onQtyChange = onQtyChange)
                    Box(modifier = Modifier.weight(1f))
                    CartLinePrice(line = line)
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CartLineInfoIcon()
                CartLineName(line = line, modifier = Modifier.weight(1f))
                QtyStepper(qty = line.displayQty, onQtyChange = onQtyChange)
                CartLinePrice(line = line)
                CartLineRemoveButton(onClick = { showRemoveConfirm = true })
            }
        }
    }

    PharmModal(
        open = showRemoveConfirm,
        onDismiss = { showRemoveConfirm = false },
        title = "ลบออกจากตะกร้า?",
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = { showRemoveConfirm = false },
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = "ลบ",
                onClick = {
                    showRemoveConfirm = false
                    onRemove()
                },
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
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
private fun CartLineInfoIcon() {
    val t = pharmTokens
    Icon(
        imageVector = Icons.Outlined.Info,
        contentDescription = "รายละเอียด",
        tint = t.colors.accent,
        modifier = Modifier.size(18.dp),
    )
}

@Composable
private fun CartLineName(line: CartLine, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
        if (line.selectedUnit == null) {
            Text(
                text = line.drug.unit ?: "ชิ้น",
                style = PharmText.micro,
                color = t.colors.fg2,
            )
        }
    }
}

@Composable
private fun CartLinePrice(line: CartLine) {
    val t = pharmTokens
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.width(96.dp),
    ) {
        Text(
            text = formatBahtCurrency(line.lineTotal),
            style = PharmText.body.tabular(),
            fontWeight = FontWeight.Bold,
            color = t.colors.fg1,
            maxLines = 1,
            textAlign = TextAlign.End,
        )
        Text(
            text = priceMetaLabel(line),
            style = PharmText.micro.tabular(),
            color = if (line.discount > 0) t.colors.dangerFg
                    else t.colors.fg2,
            maxLines = 1,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun CartLineRemoveButton(onClick: () -> Unit) {
    val t = pharmTokens
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "ลบรายการ",
            tint = t.colors.fg2,
            modifier = Modifier.size(20.dp),
        )
    }
}

private fun priceMetaLabel(line: CartLine): String {
    if (line.discount > 0) {
        val saved = line.discount * line.qty
        return "−${formatBahtCurrency(saved)}"
    }
    return when (line.tier) {
        "wholesale" -> "ราคาส่ง"
        "regular"   -> "ราคาทั่วไป"
        "retail"    -> "ราคาหน้าร้าน"
        else        -> "ราคาหน้าร้าน"
    }
}

private const val MAX_QTY = 9999

@Composable
private fun QtyStepper(
    qty: Int,
    onQtyChange: (Int) -> Unit,
) {
    val t = pharmTokens
    var editing by remember { mutableStateOf(false) }
    var draft by remember(qty) { mutableStateOf(qty.toString()) }

    fun commit() {
        val parsed = draft.toIntOrNull() ?: qty
        val clamped = parsed.coerceIn(0, MAX_QTY)
        if (clamped != qty) onQtyChange(clamped)
        draft = clamped.toString()
        editing = false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {

        StepperCircle(
            onClick = { onQtyChange(qty - 1) },
            container = t.colors.dangerBg,
            iconTint = t.colors.dangerFg,
            icon = Icons.Outlined.Remove,
            description = "ลด",
            enabled = qty > 1,
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(40.dp)
                .height(28.dp),
        ) {
            if (editing) {
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it.filter(Char::isDigit).take(4) },
                    singleLine = true,
                    textStyle = PharmText.body.tabular().copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = t.colors.fg1,
                    ),
                    cursorBrush = SolidColor(t.colors.accent),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused) commit() },
                )
            } else {
                Text(
                    text = "${qty}x",
                    style = PharmText.body.tabular(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { editing = true },
                )
            }
        }

        StepperCircle(
            onClick = { onQtyChange((qty + 1).coerceAtMost(MAX_QTY)) },
            container = t.colors.accent,
            iconTint = t.colors.surface,
            icon = Icons.Outlined.Add,
            description = "เพิ่ม",
            enabled = qty < MAX_QTY,
        )
    }
}

@Composable
private fun StepperCircle(
    onClick: () -> Unit,
    container: Color,
    iconTint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    enabled: Boolean,
) {
    val t = pharmTokens
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(44.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) container
                    else t.colors.surfaceRaised
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = if (enabled) iconTint
                       else t.colors.fg2,
                modifier = Modifier.size(16.dp),
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
    sellPrice = 25.0,
    costPrice = 12.0,
    stock = 240,
    minStock = 20,
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
            line = CartLine(drug = previewDrug(), qty = 5, discount = 3.0),
            onQtyChange = {},
            onRemove = {},
            onTapForDiscount = {},
        )
    }
}
