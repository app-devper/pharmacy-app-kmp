package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.imports.i18n.localizeImports
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun ImportFormContent(
    state: ImportFormUiState,
    callbacks: ImportFormCallbacks = ImportFormCallbacks(),
) {
    val t = pharmTokens
    val s = pharmStrings
    var pickerForLine by remember { mutableStateOf<Int?>(null) }
    var supplierPickerOpen by remember { mutableStateOf(false) }
    var validationRequested by remember(state.mode) { mutableStateOf(false) }
    val supplierFocusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = if (state.isEdit) s.importsFormEditTitle else s.importsNewTitle,
            onBack = callbacks.onBack,
            actions = {
                if (!state.readOnly) {
                    PharmSaveAction(
                        saving = state.saving,
                        canSubmit = state.canSubmit,
                        onSubmit = callbacks.onSubmit,
                        onInvalidSubmit = if (state.loading) null else {
                            {
                                validationRequested = true
                                if (state.form.supplier.isBlank()) supplierFocusRequester.requestFocus()
                            }
                        },
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                PharmFormCard(title = s.importsFormInfoSection) {
                    ImportFormHeader(
                        state = state,
                        callbacks = callbacks,
                        onPickSupplier = { supplierPickerOpen = true },
                        showValidation = validationRequested,
                        supplierFocusRequester = supplierFocusRequester,
                    )
                }
                PharmFormCard(
                    title = s.importsItemListLabel,
                    subtitle = if (state.form.items.isEmpty()) s.bulkImportEmptyDefault else "${state.form.items.size} ${s.movementsCountNoun}",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.form.items.forEachIndexed { index, fields ->
                            ImportLineCard(
                                index = index,
                                fields = fields,
                                readOnly = state.readOnly,
                                onPickDrug = { pickerForLine = index },
                                onLot = { callbacks.onLineLotNumber(index, it) },
                                onExpiry = { callbacks.onLineExpiry(index, it) },
                                onQty = { callbacks.onLineQty(index, it) },
                                onCost = { callbacks.onLineCost(index, it) },
                                onSell = { callbacks.onLineSell(index, it) },
                                onRemove = { callbacks.onRemoveLine(index) },
                                showValidation = validationRequested,
                            )
                        }
                        if (!state.readOnly) {
                            PharmButton(
                                onClick = callbacks.onAddLine,
                                variant = PharmButtonVariant.Outline,
                                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(text = s.importsActionAddLine, style = PharmText.buttonMd)
                            }
                        }
                        if (validationRequested && state.form.items.isEmpty()) {
                            Text(
                                text = s.validationRequired(s.importsItemListLabel),
                                style = PharmText.micro.copy(color = t.colors.dangerFg),
                            )
                        }
                        if (state.readOnly) {
                            ReadOnlyNote()
                        }
                    }
                }
            }
        }
    }

    pickerForLine?.let { idx ->
        ImportDrugPickerDialog(
            drugs = state.drugs,
            onDismiss = { pickerForLine = null },
            onPick = { drug ->
                callbacks.onLineDrug(idx, drug)
                pickerForLine = null
            },
        )
    }

    if (supplierPickerOpen) {
        ImportSupplierPickerDialog(
            suppliers = state.suppliers,
            onDismiss = { supplierPickerOpen = false },
            onPick = { supplier ->
                callbacks.onSupplier(supplier.name)
                supplierPickerOpen = false
            },
        )
    }

    ErrorBottomSheet(message = state.errorState?.localizeImports(s), onDismiss = callbacks.onDismissError)
}

@Composable
private fun ReadOnlyNote() {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.warningBg, t.shapes.md)
            .border(1.dp, t.colors.warningFg.copy(alpha = 0.3f), t.shapes.md)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = pharmStrings.importsFormReceivedConfirmedHint,
            style = PharmText.bodySm.copy(color = t.colors.warningFg),
        )
    }
}

@Preview
@Composable
private fun ImportFormContent_Add_Preview() {
    PharmacyTheme {
        ImportFormContent(
            state = ImportFormUiState(),
            callbacks = ImportFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportFormContent_WithItems_Preview() {
    PharmacyTheme {
        ImportFormContent(
            state = ImportFormUiState(
                form = ImportFormFields(
                    supplier = "บริษัท เอ บี ซี ฟาร์มา",
                    invoiceNo = "INV-2026-0001",
                    receiveDate = "2026-05-15",
                    items = listOf(
                        ImportLineFields(
                            drugName = "Paracetamol 500 mg",
                            lotNumber = "A12345",
                            expiryDate = "2027-12-31",
                            qty = "100",
                            costPrice = "1.00",
                            sellPrice = "2.00",
                        ),
                    ),
                ),
            ),
            callbacks = ImportFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportFormContent_ReadOnly_Preview() {
    PharmacyTheme {
        ImportFormContent(
            state = ImportFormUiState(
                mode = ImportFormMode.Edit("po1"),
                readOnly = true,
                form = ImportFormFields(
                    supplier = "บริษัท เอ บี ซี ฟาร์มา",
                    items = listOf(
                        ImportLineFields(drugName = "Amoxicillin", qty = "50"),
                    ),
                ),
            ),
            callbacks = ImportFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportFormContent_Loading_Preview() {
    PharmacyTheme {
        ImportFormContent(
            state = ImportFormUiState(loading = true),
            callbacks = ImportFormCallbacks(),
        )
    }
}
