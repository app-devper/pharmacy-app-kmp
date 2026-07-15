package app.devper.pharm.presentation.stock.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stock.AltUnitDraft
import app.devper.pharm.presentation.stock.DrugFormFields
import app.devper.pharm.ui.common.pharmToggleable
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCheckbox
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

internal class AltUnitFocusRequesters {
    val name = FocusRequester()
    val factor = FocusRequester()
    val sellPrice = FocusRequester()
}

@Composable
internal fun DrugFormPricingSections(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    showValidation: Boolean,
    tierFocus: TierPriceFocusRequesters,
    altUnitFocus: List<AltUnitFocusRequesters>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TierPricingCard(form = form, callbacks = callbacks, focus = tierFocus)
        AltUnitsCard(
            form = form,
            callbacks = callbacks,
            showValidation = showValidation,
            focusRequesters = altUnitFocus,
        )
    }
}

@Composable
private fun AltUnitsCard(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    showValidation: Boolean,
    focusRequesters: List<AltUnitFocusRequesters>,
) {
    val t = pharmTokens
    PharmFormCard(
        title = pharmStrings.stockAltUnitsTitle,
        subtitle = pharmStrings.stockAltUnitsSubtitle,
    ) {
        if (form.altUnits.isEmpty()) {
            Text(
                text = pharmStrings.stockAltUnitEmpty,
                style = PharmText.body.copy(color = t.colors.fg2),
            )
        } else {
            form.altUnits.forEachIndexed { index, unit ->
                AltUnitEditor(
                    index = index,
                    unit = unit,
                    form = form,
                    callbacks = callbacks,
                    showValidation = showValidation,
                    focus = focusRequesters[index],
                )
            }
        }
        PharmButton(
            label = pharmStrings.stockAltUnitAdd,
            onClick = callbacks.onAddAltUnit,
            variant = PharmButtonVariant.Outline,
            leadingIcon = {
                Icon(
                    imageVector = PharmIcons.Plus,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
        )
    }
}

@Composable
private fun AltUnitEditor(
    index: Int,
    unit: AltUnitDraft,
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    showValidation: Boolean,
    focus: AltUnitFocusRequesters,
) {
    val t = pharmTokens
    val nameError = altUnitNameError(form, index, showValidation)
    val factorError = altUnitFactorError(unit, showValidation)
    val sellPriceError = if (unit.sellPriceValid) null else {
        pharmStrings.validationNotANumber(pharmStrings.stockHeaderSellPrice)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.bgPage, t.shapes.md)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = pharmStrings.stockAltUnitTitle(index + 1),
                style = PharmText.h3,
            )
            PharmButton(
                label = pharmStrings.commonDelete,
                onClick = { callbacks.onRemoveAltUnit(index) },
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = {
                    Icon(
                        imageVector = PharmIcons.Trash,
                        contentDescription = null,
                        tint = t.colors.dangerFg,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth >= PharmBreakpoint.FormTwoCol) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AltUnitFieldRow(
                        left = {
                            AltUnitNameField(unit, index, callbacks, nameError, focus.name)
                        },
                        right = {
                            AltUnitFactorField(unit, index, callbacks, factorError, focus.factor)
                        },
                    )
                    AltUnitFieldRow(
                        left = {
                            AltUnitSellPriceField(unit, index, callbacks, sellPriceError, focus.sellPrice)
                        },
                        right = { AltUnitBarcodeField(unit, index, callbacks) },
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AltUnitNameField(unit, index, callbacks, nameError, focus.name)
                    AltUnitFactorField(unit, index, callbacks, factorError, focus.factor)
                    AltUnitSellPriceField(unit, index, callbacks, sellPriceError, focus.sellPrice)
                    AltUnitBarcodeField(unit, index, callbacks)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(t.shapes.md)
                .pharmToggleable(
                    value = unit.hidden,
                    role = Role.Checkbox,
                    shape = t.shapes.md,
                    onValueChange = { callbacks.onAltUnitHidden(index, it) },
                )
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmCheckbox(
                checked = unit.hidden,
                onCheckedChange = null,
            )
            Text(text = pharmStrings.stockAltUnitHidden, style = PharmText.body)
        }
    }
}

@Composable
private fun AltUnitFieldRow(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}

@Composable
private fun altUnitNameError(form: DrugFormFields, index: Int, showValidation: Boolean): String? {
    if (!showValidation || form.altUnitNameValid(index)) return null
    val name = form.altUnits[index].name.trim()
    return when {
        name.isBlank() -> pharmStrings.validationRequired(pharmStrings.stockAltUnitName)
        name.equals(form.unit.trim(), ignoreCase = true) -> pharmStrings.stockAltUnitNameMatchesBase
        else -> pharmStrings.stockAltUnitNameDuplicate
    }
}

@Composable
private fun altUnitFactorError(unit: AltUnitDraft, showValidation: Boolean): String? {
    if (unit.factorValid || (!showValidation && unit.factor.isBlank())) return null
    return if (unit.factor.isBlank()) {
        pharmStrings.validationRequired(pharmStrings.stockAltUnitFactor)
    } else {
        pharmStrings.stockAltUnitFactorInvalid
    }
}

@Composable
private fun AltUnitNameField(
    unit: AltUnitDraft,
    index: Int,
    callbacks: DrugFormCallbacks,
    error: String?,
    focusRequester: FocusRequester,
) {
    FormField(
        label = pharmStrings.stockAltUnitName,
        required = true,
        error = error,
    ) {
        PharmTextField(
            value = unit.name,
            onValueChange = { callbacks.onAltUnitName(index, it) },
            placeholder = pharmStrings.commonUnit,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun AltUnitFactorField(
    unit: AltUnitDraft,
    index: Int,
    callbacks: DrugFormCallbacks,
    error: String?,
    focusRequester: FocusRequester,
) {
    FormField(
        label = pharmStrings.stockAltUnitFactor,
        required = true,
        hint = if (error == null) pharmStrings.stockAltUnitFactorHint else null,
        error = error,
    ) {
        PharmTextField(
            value = unit.factor,
            onValueChange = { callbacks.onAltUnitFactor(index, it) },
            placeholder = "10",
            keyboardType = KeyboardType.Number,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun AltUnitSellPriceField(
    unit: AltUnitDraft,
    index: Int,
    callbacks: DrugFormCallbacks,
    error: String?,
    focusRequester: FocusRequester,
) {
    FormField(
        label = pharmStrings.stockHeaderSellPrice,
        error = error,
    ) {
        PharmTextField(
            value = unit.sellPrice,
            onValueChange = { callbacks.onAltUnitSellPrice(index, it) },
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun AltUnitBarcodeField(
    unit: AltUnitDraft,
    index: Int,
    callbacks: DrugFormCallbacks,
) {
    FormField(label = pharmStrings.stockHeaderBarcode) {
        PharmTextField(
            value = unit.barcode,
            onValueChange = { callbacks.onAltUnitBarcode(index, it) },
            placeholder = pharmStrings.stockExampleBarcode,
        )
    }
}
