package app.devper.pharm.presentation.imports

import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.domain.validation.Check
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

@Composable
internal fun ImportLineCard(
    index: Int,
    fields: ImportLineFields,
    readOnly: Boolean,
    onPickDrug: () -> Unit,
    onLot: (String) -> Unit,
    onExpiry: (String) -> Unit,
    onQty: (String) -> Unit,
    onCost: (String) -> Unit,
    onSell: (String) -> Unit,
    onRemove: () -> Unit,
    showValidation: Boolean = false,
) {
    val t = pharmTokens
    val s = pharmStrings
    val drugError = showValidation && fields.drugId.isBlank()
    val lotError = showValidation && fields.lotNumber.isBlank()
    val expiryError = showValidation && !Check.localDate(fields.expiryDate)
    val qtyError = showValidation && !Check.positiveInt(fields.qty)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.surface, t.shapes.md)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "#${index + 1}",
                style = PharmText.meta.copy(color = t.colors.fg3),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(t.shapes.md)
                    .background(if (readOnly) t.colors.borderSubtle else t.colors.bgPage, t.shapes.md)
                    .border(1.dp, if (drugError) t.colors.dangerFg else t.colors.border, t.shapes.md)
                    .pharmClickable(enabled = !readOnly, onClick = onPickDrug)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = fields.drugName.ifBlank { pharmStrings.importsFormPickDrugPlaceholder },
                    style = PharmText.body.copy(
                        color = if (fields.drugName.isBlank()) t.colors.fgMuted else t.colors.fg1,
                    ),
                )
            }
            if (!readOnly) {
                PharmButton(
                    onClick = onRemove,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Sm,
                ) {
                    Icon(
                        imageVector = PharmIcons.Trash,
                        contentDescription = pharmStrings.importsActionRemoveLine,
                        tint = t.colors.dangerFg,
                    )
                }
            }
        }
        if (drugError) {
            Text(
                text = s.validationRequired(s.fieldDrug),
                style = PharmText.micro.copy(color = t.colors.dangerFg),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ImportLabeledField(
                label = s.importsFormHeaderLotNumber,
                required = true,
                error = if (lotError) s.validationRequired(s.importsFormHeaderLotNumber) else null,
                modifier = Modifier.weight(1f),
            ) {
                ImportFormField(
                    value = fields.lotNumber,
                    onValueChange = onLot,
                    placeholder = s.importsFormHeaderLotNumberPlaceholder,
                    enabled = !readOnly,
                    isError = lotError,
                )
            }
            ImportLabeledField(
                label = s.importsExpiryDateLabel,
                required = true,
                error = if (expiryError) s.validationInvalidDate(s.importsExpiryDateLabel) else null,
                modifier = Modifier.weight(1f),
            ) {
                ImportFormField(
                    value = fields.expiryDate,
                    onValueChange = onExpiry,
                    placeholder = "YYYY-MM-DD",
                    enabled = !readOnly,
                    isError = expiryError,
                )
            }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val threeCol = maxWidth >= PharmBreakpoint.Medium
            if (threeCol) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImportLabeledField(
                        label = s.commonQty,
                        required = true,
                        error = if (qtyError) s.validationMustBePositive(s.commonQty) else null,
                        modifier = Modifier.weight(1f),
                    ) {
                        QtyField(fields, onQty, readOnly, qtyError)
                    }
                    ImportLabeledField(label = s.importsFormHeaderCostPrice, modifier = Modifier.weight(1f)) {
                        CostField(fields, onCost, readOnly)
                    }
                    ImportLabeledField(label = s.importsFormHeaderSellPrice, modifier = Modifier.weight(1f)) {
                        SellField(fields, onSell, readOnly)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImportLabeledField(
                        label = s.commonQty,
                        required = true,
                        error = if (qtyError) s.validationMustBePositive(s.commonQty) else null,
                    ) {
                        QtyField(fields, onQty, readOnly, qtyError)
                    }
                    ImportLabeledField(label = s.importsFormHeaderCostPrice) {
                        CostField(fields, onCost, readOnly)
                    }
                    ImportLabeledField(label = s.importsFormHeaderSellPrice) {
                        SellField(fields, onSell, readOnly)
                    }
                }
            }
        }
    }
}

@Composable
private fun QtyField(fields: ImportLineFields, onQty: (String) -> Unit, readOnly: Boolean, isError: Boolean) {
    ImportFormField(
        value = fields.qty,
        onValueChange = onQty,
        placeholder = "0",
        keyboardType = KeyboardType.Number,
        enabled = !readOnly,
        isError = isError,
    )
}

@Composable
private fun CostField(fields: ImportLineFields, onCost: (String) -> Unit, readOnly: Boolean) {
    ImportFormField(
        value = fields.costPrice,
        onValueChange = onCost,
        placeholder = "0.00",
        keyboardType = KeyboardType.Decimal,
        enabled = !readOnly,
    )
}

@Composable
private fun SellField(fields: ImportLineFields, onSell: (String) -> Unit, readOnly: Boolean) {
    ImportFormField(
        value = fields.sellPrice,
        onValueChange = onSell,
        placeholder = "default",
        keyboardType = KeyboardType.Decimal,
        enabled = !readOnly,
    )
}
