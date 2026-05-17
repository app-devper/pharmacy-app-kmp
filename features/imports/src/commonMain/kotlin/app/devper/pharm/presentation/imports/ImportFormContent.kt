package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportFormContent(
    state: ImportFormUiState,
    callbacks: ImportFormCallbacks = ImportFormCallbacks(),
) {
    val t = pharmTokens
    var pickerForLine by remember { mutableStateOf<Int?>(null) }
    var supplierPickerOpen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = t.colors.bgPage,
        topBar = {
            TopAppBar(
                title = { Text(text = state.titleLabel, style = PharmText.h1) },
                navigationIcon = {
                    IconButton(onClick = callbacks.onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                actions = {
                    if (state.saving) {
                        CircularProgressIndicator(
                            color = t.colors.accent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp).padding(end = 12.dp),
                        )
                    } else if (!state.readOnly) {
                        PharmButton(
                            label = "บันทึก",
                            onClick = callbacks.onSubmit,
                            enabled = state.canSubmit,
                            size = PharmButtonSize.Sm,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = t.colors.surface,
                    titleContentColor = t.colors.fg1,
                ),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = t.colors.accent)
                }
            } else {
                ImportFormBody(
                    state = state,
                    callbacks = callbacks,
                    onPickDrug = { idx -> pickerForLine = idx },
                    onPickSupplier = { supplierPickerOpen = true },
                )
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

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun ImportFormBody(
    state: ImportFormUiState,
    callbacks: ImportFormCallbacks,
    onPickDrug: (lineIndex: Int) -> Unit,
    onPickSupplier: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item("header") { ImportFormHeader(state, callbacks, onPickSupplier) }

        item("section") {
            Text(
                text = "รายการสินค้า",
                style = PharmText.h3,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        items(state.form.items.size) { index ->
            ImportLineCard(
                index = index,
                fields = state.form.items[index],
                readOnly = state.readOnly,
                onPickDrug = { onPickDrug(index) },
                onLot = { callbacks.onLineLotNumber(index, it) },
                onExpiry = { callbacks.onLineExpiry(index, it) },
                onQty = { callbacks.onLineQty(index, it) },
                onCost = { callbacks.onLineCost(index, it) },
                onSell = { callbacks.onLineSell(index, it) },
                onRemove = { callbacks.onRemoveLine(index) },
            )
        }

        item("add-line") {
            if (!state.readOnly) {
                PharmButton(
                    onClick = callbacks.onAddLine,
                    variant = PharmButtonVariant.Outline,
                    leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "เพิ่มรายการ", style = PharmText.buttonMd)
                }
            }
        }

        item("readonly-note") {
            if (state.readOnly) {
                ReadOnlyNote()
            }
        }
    }
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
            text = "ใบนี้ยืนยันแล้ว — แก้ไขไม่ได้",
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
