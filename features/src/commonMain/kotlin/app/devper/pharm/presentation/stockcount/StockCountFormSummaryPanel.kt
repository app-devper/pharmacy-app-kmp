package app.devper.pharm.presentation.stockcount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class StockCountHistoryEntry(
    val countNo: String,
    val at: String,
    val itemsCount: Int,
    val totalDelta: Int,
)

@Composable
internal fun StockCountFormSummaryPanel(
    state: StockCountFormUiState,
    callbacks: StockCountFormCallbacks,
    history: List<StockCountHistoryEntry> = emptyList(),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SummaryCard(state = state, callbacks = callbacks)
        if (history.isNotEmpty()) {
            HistoryCard(history = history)
        }
    }
}

@Composable
private fun SummaryCard(state: StockCountFormUiState, callbacks: StockCountFormCallbacks) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "บันทึกรอบนี้", style = PharmText.h3.copy(color = t.colors.fg1))
        Spacer(modifier = Modifier.height(4.dp))
        SummaryRow(label = "ทั้งหมด", value = "${state.drugs.size} รายการ")
        SummaryRow(label = "พิมพ์แล้ว", value = "${state.pendingLines.size}")
        SummaryRow(label = "รายการที่เปลี่ยน", value = "${state.changedCount}")
        SummaryRow(
            label = "ส่วนต่างรวม (abs)",
            value = state.totalAbsDelta.toString(),
            highlight = state.totalAbsDelta > 0,
        )

        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "หมายเหตุ",
            style = PharmText.micro.copy(color = t.colors.fg3),
        )
        Box(modifier = Modifier.heightIn(min = 72.dp)) {
            PharmTextField(
                value = state.note,
                onValueChange = callbacks.onNotesChange,
                placeholder = "เช่น ตรวจประจำเดือน…",
                singleLine = false,
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, highlight: Boolean = false) {
    val t = pharmTokens
    val valueColor = if (highlight) t.colors.warningFg else t.colors.fg1
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = PharmText.meta.copy(color = t.colors.fg3),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = PharmText.bodySm.copy(color = valueColor, fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun HistoryCard(history: List<StockCountHistoryEntry>) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "ประวัติตรวจนับ", style = PharmText.h3.copy(color = t.colors.fg1))
        history.forEach { entry -> HistoryRow(entry = entry) }
    }
}

@Composable
private fun HistoryRow(entry: StockCountHistoryEntry) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = entry.countNo,
            style = PharmText.micro.copy(
                color = t.colors.fg2,
                fontFamily = FontFamily.Monospace,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "${entry.at} · ${entry.itemsCount} รายการ",
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
            val deltaColor = if (entry.totalDelta == 0) t.colors.fgMuted else t.colors.warningFg
            val deltaText = if (entry.totalDelta == 0) "0" else "±${entry.totalDelta}"
            Text(
                text = deltaText,
                style = PharmText.micro.copy(color = deltaColor, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}
