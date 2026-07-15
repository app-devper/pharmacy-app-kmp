package app.devper.pharm.presentation.stock.form

import app.devper.pharm.ui.components.PharmBreakpoint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stock.DrugFormFields
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun DrugFormInitialStockSection(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    modifier: Modifier = Modifier,
    showValidation: Boolean = false,
    lotNumberFocusRequester: FocusRequester = FocusRequester.Default,
    lotExpiryFocusRequester: FocusRequester = FocusRequester.Default,
    lotCostPriceFocusRequester: FocusRequester = FocusRequester.Default,
    lotSellPriceFocusRequester: FocusRequester = FocusRequester.Default,
) {
    PharmFormCard(
        title = pharmStrings.stockHeaderInitialStock,
        subtitle = pharmStrings.stockHeaderInitialLotHint,
        modifier = modifier,
    ) {
        InitialStockGrid(
            form = form,
            callbacks = callbacks,
            showValidation = showValidation,
            lotNumberFocusRequester = lotNumberFocusRequester,
            lotExpiryFocusRequester = lotExpiryFocusRequester,
        )
        if (form.hasInitialStock) {
            InitialLotPriceFields(
                form = form,
                callbacks = callbacks,
                lotCostPriceFocusRequester = lotCostPriceFocusRequester,
                lotSellPriceFocusRequester = lotSellPriceFocusRequester,
            )
        }
    }
}

@Composable
private fun InitialLotPriceFields(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    lotCostPriceFocusRequester: FocusRequester,
    lotSellPriceFocusRequester: FocusRequester,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth >= PharmBreakpoint.FormTwoCol) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    InitialLotCostPriceField(form, callbacks, lotCostPriceFocusRequester)
                }
                Box(modifier = Modifier.weight(1f)) {
                    InitialLotSellPriceField(form, callbacks, lotSellPriceFocusRequester)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InitialLotCostPriceField(form, callbacks, lotCostPriceFocusRequester)
                InitialLotSellPriceField(form, callbacks, lotSellPriceFocusRequester)
            }
        }
    }
}

@Composable
private fun InitialLotCostPriceField(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    focusRequester: FocusRequester,
) {
    val s = pharmStrings
    val error = if (form.initialLotCostPriceValid) null else {
        s.validationNotANumber(s.stockHeaderCostPrice)
    }
    FormField(
        label = s.stockHeaderCostPrice,
        hint = if (error == null) s.stockLotInitialCostHint else null,
        error = error,
    ) {
        PharmTextField(
            value = form.lotCostPrice,
            onValueChange = callbacks.onLotCostPrice,
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun InitialLotSellPriceField(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    focusRequester: FocusRequester,
) {
    val s = pharmStrings
    val error = if (form.initialLotSellPriceValid) null else {
        s.validationNotANumber(s.stockHeaderSellPrice)
    }
    FormField(
        label = s.stockHeaderSellPrice,
        hint = if (error == null) s.stockLotInitialSellHint else null,
        error = error,
    ) {
        PharmTextField(
            value = form.lotSellPrice,
            onValueChange = callbacks.onLotSellPrice,
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun InitialStockGrid(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    showValidation: Boolean,
    lotNumberFocusRequester: FocusRequester,
    lotExpiryFocusRequester: FocusRequester,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val threeCol = maxWidth >= PharmBreakpoint.FormThreeCol
        if (threeCol) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) { QuantityField(form, callbacks) }
                Box(modifier = Modifier.weight(1f)) {
                    LotNumberField(form, callbacks, showValidation, lotNumberFocusRequester)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ExpiryField(form, callbacks, showValidation, lotExpiryFocusRequester)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                QuantityField(form, callbacks)
                LotNumberField(form, callbacks, showValidation, lotNumberFocusRequester)
                ExpiryField(form, callbacks, showValidation, lotExpiryFocusRequester)
            }
        }
    }
}

@Composable
private fun QuantityField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.commonQty) {
        PharmTextField(
            value = form.initialStock,
            onValueChange = callbacks.onInitialStock,
            keyboardType = KeyboardType.Number,
            placeholder = "0",
        )
    }
}

@Composable
private fun LotNumberField(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    showValidation: Boolean,
    focusRequester: FocusRequester,
) {
    val s = pharmStrings
    val error = if (showValidation && !form.initialLotNumberValid) {
        s.validationRequired(s.fieldLotNumber)
    } else null
    FormField(
        label = s.stockLotNumber,
        required = form.hasInitialStock,
        error = error,
    ) {
        PharmTextField(
            value = form.lotNumber,
            onValueChange = callbacks.onLotNumber,
            placeholder = s.stockExampleLotNo,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun ExpiryField(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    showValidation: Boolean,
    focusRequester: FocusRequester,
) {
    val s = pharmStrings
    val showError = form.hasInitialStock && !form.initialLotExpiryValid &&
        (showValidation || form.lotExpiry.isNotBlank())
    val error = if (showError) {
        if (form.lotExpiry.isBlank()) {
            s.validationRequired(s.fieldExpiryDate)
        } else {
            s.validationInvalidDate(s.fieldExpiryDate)
        }
    } else null
    FormField(
        label = s.importsExpiryDateLabel,
        required = form.hasInitialStock,
        hint = if (error == null) "YYYY-MM-DD" else null,
        error = error,
    ) {
        PharmTextField(
            value = form.lotExpiry,
            onValueChange = callbacks.onLotExpiry,
            placeholder = "2026-12-31",
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}
