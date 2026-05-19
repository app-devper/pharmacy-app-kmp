package app.devper.pharm.presentation.labels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun LabelPrintContent(
    state: LabelPrintUiState,
    callbacks: LabelPrintCallbacks = LabelPrintCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabelDrugPicker(
                state = state,
                onQueryChange = callbacks.onQueryChange,
                onAddDrug = callbacks.onAddDrug,
                modifier = Modifier.weight(2f).fillMaxHeight(),
            )

            Column(
                modifier = Modifier.weight(3f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LabelToolbar(
                    size = state.size,
                    totalCopies = state.totalCopies,
                    canPrint = state.canPrint,
                    printing = state.printing,
                    onSizeChange = callbacks.onSizeChange,
                    onClearAll = callbacks.onClearAll,
                    onPrint = callbacks.onPrint,
                )
                LabelLinesTable(
                    lines = state.lines,
                    onRemoveLine = callbacks.onRemoveLine,
                    onChangeCopies = callbacks.onChangeCopies,
                    onChangeBarcode = callbacks.onChangeBarcode,
                    onToggleIncludePrice = callbacks.onToggleIncludePrice,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                )
                LabelPreviewGrid(
                    size = state.size,
                    line = state.previewLine,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (state.message != null) {
        ErrorBottomSheet(message = state.message, onDismiss = callbacks.onDismissMessage, title = "พิมพ์สำเร็จ")
    }
    if (state.error != null) {
        ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
    }
}

@Composable
private fun LabelDrugPicker(
    state: LabelPrintUiState,
    onQueryChange: (String) -> Unit,
    onAddDrug: (Drug) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            PharmTextField(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = "ค้นหายา (เพิ่มทีละบรรทัด)…",
            )
        }
        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.filteredDrugs, key = { it.id }) { drug ->
                LabelDrugRow(drug = drug, onAdd = { onAddDrug(drug) })
            }
        }
    }
}

@Composable
private fun LabelDrugRow(drug: Drug, onAdd: () -> Unit) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAdd)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = drug.name,
            style = PharmText.body.copy(color = t.colors.fg1),
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Text(
            text = "+ เพิ่ม",
            style = PharmText.micro.copy(color = t.colors.accent, fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun LabelToolbar(
    size: LabelSize,
    totalCopies: Int,
    canPrint: Boolean,
    printing: Boolean,
    onSizeChange: (LabelSize) -> Unit,
    onClearAll: () -> Unit,
    onPrint: () -> Unit,
) {
    val t = pharmTokens
    val chips = remember(t) {
        LabelSize.entries.map { PharmFilterChip(id = it.wire, label = it.label) }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "ขนาดฉลาก",
            style = PharmText.micro.copy(color = t.colors.fg2),
        )
        PharmSingleSelectChips(
            chips = chips,
            activeId = size.wire,
            onSelect = { id -> onSizeChange(LabelSize.fromWire(id)) },
        )
        Spacer(modifier = Modifier.weight(1f))
        PharmButton(
            label = "ล้าง",
            onClick = onClearAll,
            variant = PharmButtonVariant.Ghost,
            size = PharmButtonSize.Sm,
            enabled = !printing,
        )
        PharmButton(
            label = if (printing) "กำลังพิมพ์…" else "พิมพ์ $totalCopies ดวง",
            onClick = onPrint,
            variant = PharmButtonVariant.Primary,
            size = PharmButtonSize.Sm,
            enabled = canPrint,
        )
    }
}

@Composable
private fun LabelLinesTable(
    lines: List<LabelLine>,
    onRemoveLine: (Int) -> Unit,
    onChangeCopies: (Int, Int) -> Unit,
    onChangeBarcode: (Int, String) -> Unit,
    onToggleIncludePrice: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(t.colors.bgPage)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = "รายการฉลาก (${lines.size} บรรทัด)",
                style = PharmText.micro.copy(color = t.colors.fg2, fontWeight = FontWeight.SemiBold),
            )
        }
        if (lines.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "ยังไม่มีรายการ เลือกยาทางซ้ายเพื่อเพิ่ม",
                    style = PharmText.body.copy(color = t.colors.fg3),
                )
            }
            return@Column
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(lines, key = { _, line -> line.drugId + line.hashCode() }) { index, line ->
                LabelLineRow(
                    line = line,
                    onRemove = { onRemoveLine(index) },
                    onCopiesChange = { onChangeCopies(index, it) },
                    onBarcodeChange = { onChangeBarcode(index, it) },
                    onIncludePriceChange = { onToggleIncludePrice(index, it) },
                )
            }
        }
    }
}

@Composable
private fun LabelLineRow(
    line: LabelLine,
    onRemove: () -> Unit,
    onCopiesChange: (Int) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onIncludePriceChange: (Boolean) -> Unit,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(3f)) {
            Text(
                text = line.drugName,
                style = PharmText.body.copy(color = t.colors.fg1, fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
            Text(
                text = "ล็อต: ${line.lotNumber.ifBlank { "(ไม่ระบุ)" }}",
                style = PharmText.micro.copy(color = t.colors.fg3),
            )
        }
        PharmTextField(
            value = line.barcode,
            onValueChange = onBarcodeChange,
            modifier = Modifier.weight(2f),
        )
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "จำนวน",
                style = PharmText.micro.copy(color = t.colors.fg2),
            )
            PharmTextField(
                value = line.copies.toString(),
                onValueChange = { raw ->
                    val n = raw.filter { it.isDigit() }.toIntOrNull() ?: 0
                    onCopiesChange(n)
                },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.width(72.dp),
            )
        }
        Row(
            modifier = Modifier
                .weight(2f)
                .clickable { onIncludePriceChange(!line.includePrice) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(t.shapes.sm)
                    .background(if (line.includePrice) t.colors.accent else t.colors.surface)
                    .border(1.dp, t.colors.border, t.shapes.sm),
                contentAlignment = Alignment.Center,
            ) {
                if (line.includePrice) {
                    Text(
                        text = "✓",
                        style = PharmText.micro.copy(color = t.colors.surface, fontWeight = FontWeight.Bold),
                    )
                }
            }
            Text(
                text = "รวมราคา",
                style = PharmText.micro.copy(color = t.colors.fg2),
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(t.shapes.sm)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×",
                style = PharmText.h3.copy(color = t.colors.fg3),
            )
        }
    }
}

@Composable
private fun LabelPreviewGrid(size: LabelSize, line: LabelLine?, modifier: Modifier = Modifier) {
    val t = pharmTokens
    if (line == null) return
    Column(
        modifier = modifier
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "ตัวอย่าง (${size.label})",
            style = PharmText.micro.copy(color = t.colors.fg2, fontWeight = FontWeight.SemiBold),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(6) { LabelPreviewCard(size = size, line = line) }
        }
    }
}

@Composable
private fun LabelPreviewCard(size: LabelSize, line: LabelLine) {
    val t = pharmTokens
    val cardW = (size.widthMm * 3).dp
    val cardH = (size.heightMm * 3).dp
    Column(
        modifier = Modifier
            .width(cardW)
            .height(cardH)
            .border(1.dp, t.colors.border, t.shapes.sm)
            .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = line.drugName,
            style = PharmText.micro.copy(color = t.colors.fg1, fontWeight = FontWeight.SemiBold),
            maxLines = 2,
        )
        Column {
            if (line.includePrice) {
                Text(
                    text = "฿${formatMoney(line.price)}",
                    style = PharmText.body.copy(color = t.colors.accent, fontWeight = FontWeight.Bold),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(t.colors.fg1),
            )
            Text(
                text = line.barcode,
                style = PharmText.micro.copy(color = t.colors.fg3),
                maxLines = 1,
            )
        }
    }
}

private fun formatMoney(v: Double): String {
    val cents = (v * 100.0 + if (v >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).let { if (it < 0) -it else it }.toString().padStart(2, '0')
    return "$whole.$frac"
}
