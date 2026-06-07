package app.devper.pharm.presentation.stock.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stock.DrugFormFields
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.KyBadge
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings

private val kyForms = listOf(9, 10, 11, 12)
private fun kyCode(form: Int) = "ky$form"

@Composable
fun DrugFormDrugInfoSection(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmFormCard(modifier = modifier, title = pharmStrings.importsFormInfoSection) {
        DrugInfoGrid(form = form, callbacks = callbacks)
        KyChecklist(
            selected = form.reportTypes,
            onToggle = callbacks.onToggleReportType,
        )
    }
}

@Composable
private fun DrugInfoGrid(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val twoCol = maxWidth >= 560.dp
        if (twoCol) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GridRow(
                    left = { TradeNameField(form, callbacks) },
                    right = { GenericNameField(form, callbacks) },
                )
                GridRow(
                    left = { StrengthField(form, callbacks) },
                    right = { UnitField(form, callbacks) },
                )
                GridRow(
                    left = { TypeField(form, callbacks) },
                    right = { RegNoField(form, callbacks) },
                )
                GridRow(
                    left = { BarcodeField(form, callbacks) },
                    right = { CostPriceField(form, callbacks) },
                )
                GridRow(
                    left = { SellPriceField(form, callbacks) },
                    right = { MinStockField(form, callbacks) },
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TradeNameField(form, callbacks)
                GenericNameField(form, callbacks)
                StrengthField(form, callbacks)
                UnitField(form, callbacks)
                TypeField(form, callbacks)
                RegNoField(form, callbacks)
                BarcodeField(form, callbacks)
                CostPriceField(form, callbacks)
                SellPriceField(form, callbacks)
                MinStockField(form, callbacks)
            }
        }
    }
}

@Composable
private fun GridRow(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}

@Composable
private fun TradeNameField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderName, required = true) {
        PharmTextField(
            value = form.name,
            onValueChange = callbacks.onName,
            placeholder = pharmStrings.stockExampleBrand,
        )
    }
}

@Composable
private fun GenericNameField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderGeneric) {
        PharmTextField(
            value = form.genericName,
            onValueChange = callbacks.onGenericName,
            placeholder = pharmStrings.stockExampleGeneric,
        )
    }
}

@Composable
private fun StrengthField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderStrength) {
        PharmTextField(
            value = form.strength,
            onValueChange = callbacks.onStrength,
            placeholder = pharmStrings.stockExampleStrength,
        )
    }
}

@Composable
private fun UnitField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.commonUnit) {
        PharmTextField(
            value = form.unit,
            onValueChange = callbacks.onUnit,
            placeholder = "เม็ด / แคปซูล / ขวด",
        )
    }
}

@Composable
private fun TypeField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderCategory) {
        PharmTextField(
            value = form.type,
            onValueChange = callbacks.onType,
            placeholder = pharmStrings.stockTypeBoth,
        )
    }
}

@Composable
private fun RegNoField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderRegistration) {
        PharmTextField(
            value = form.regNo,
            onValueChange = callbacks.onRegNo,
            placeholder = pharmStrings.stockExampleReg,
        )
    }
}

@Composable
private fun BarcodeField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderBarcode) {
        PharmTextField(
            value = form.barcode,
            onValueChange = callbacks.onBarcode,
            placeholder = pharmStrings.stockBarcodeScanHint,
        )
    }
}

@Composable
private fun CostPriceField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderCostPrice) {
        PharmTextField(
            value = form.costPrice,
            onValueChange = callbacks.onCostPrice,
            keyboardType = KeyboardType.Decimal,
            placeholder = "0.00",
        )
    }
}

@Composable
private fun SellPriceField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockHeaderSellPrice, required = true) {
        PharmTextField(
            value = form.sellPrice,
            onValueChange = callbacks.onSellPrice,
            keyboardType = KeyboardType.Decimal,
            placeholder = "0.00",
        )
    }
}

@Composable
private fun MinStockField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = "threshold ใกล้หมด") {
        PharmTextField(
            value = form.minStock,
            onValueChange = callbacks.onMinStock,
            keyboardType = KeyboardType.Number,
            placeholder = "20",
        )
    }
}

@Composable
private fun KyChecklist(
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = pharmStrings.stockHeaderReportsMultiHint,
            style = PharmText.h3.copy(color = t.colors.fg2),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            kyForms.forEach { form ->
                KyChip(
                    form = form,
                    checked = kyCode(form) in selected,
                    onToggle = { onToggle(kyCode(form)) },
                )
            }
        }
    }
}

@Composable
private fun KyChip(
    form: Int,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    val t = pharmTokens
    val borderColor = if (checked) t.colors.accent else t.colors.border
    val bg = if (checked) t.colors.accentBgSoft else t.colors.surface

    Row(
        modifier = Modifier
            .clip(t.shapes.pill)
            .background(bg, t.shapes.pill)
            .border(1.dp, borderColor, t.shapes.pill)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CheckMark(checked = checked)
        KyBadge(form = form)
    }
}

@Composable
private fun CheckMark(checked: Boolean) {
    val t = pharmTokens
    val shape = RoundedCornerShape(3.dp)
    val borderColor = if (checked) t.colors.accent else t.colors.border
    val fill = if (checked) t.colors.accent else Color.Transparent

    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(shape)
            .background(fill, shape)
            .border(1.dp, borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Box(
                modifier = Modifier
                    .padding(start = 2.dp, top = 1.dp)
                    .size(width = 4.dp, height = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .padding(start = 1.dp)
                        .background(t.colors.surface),
                )
            }
        }
    }
}

