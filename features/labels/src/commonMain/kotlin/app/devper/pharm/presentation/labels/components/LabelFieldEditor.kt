package app.devper.pharm.presentation.labels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun LabelFieldEditor(
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
            itemsIndexed(lines, key = { _, line -> line.drugId }) { index, line ->
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
                .toggleable(
                    value = line.includePrice,
                    role = Role.Checkbox,
                    onValueChange = { onIncludePriceChange(it) },
                ),
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
                .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                .clip(t.shapes.sm)
                .clickable(onClick = onRemove)
                .semantics {
                    contentDescription = "ลบบรรทัด"
                    role = Role.Button
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×",
                style = PharmText.h3.copy(color = t.colors.fg3),
            )
        }
    }
}
