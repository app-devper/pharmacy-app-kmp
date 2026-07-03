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
) {
    PharmFormCard(
        title = pharmStrings.stockHeaderInitialStock,
        subtitle = pharmStrings.stockHeaderInitialLotHint,
        modifier = modifier,
    ) {
        InitialStockGrid(form = form, callbacks = callbacks)
    }
}

@Composable
private fun InitialStockGrid(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val threeCol = maxWidth >= PharmBreakpoint.FormThreeCol
        if (threeCol) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) { QuantityField(form, callbacks) }
                Box(modifier = Modifier.weight(1f)) { LotNumberField(form, callbacks) }
                Box(modifier = Modifier.weight(1f)) { ExpiryField(form, callbacks) }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                QuantityField(form, callbacks)
                LotNumberField(form, callbacks)
                ExpiryField(form, callbacks)
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
private fun LotNumberField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.stockLotNumber) {
        PharmTextField(
            value = form.lotNumber,
            onValueChange = callbacks.onLotNumber,
            placeholder = pharmStrings.stockExampleLotNo,
        )
    }
}

@Composable
private fun ExpiryField(form: DrugFormFields, callbacks: DrugFormCallbacks) {
    FormField(label = pharmStrings.importsExpiryDateLabel, hint = "YYYY-MM-DD") {
        PharmTextField(
            value = form.lotExpiry,
            onValueChange = callbacks.onLotExpiry,
            placeholder = "2026-12-31",
        )
    }
}
