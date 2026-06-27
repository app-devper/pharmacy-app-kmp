package app.devper.pharm.presentation.bulkimport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun BulkImportResultSummary(result: BulkImportResult) {
    val t = pharmTokens
    val s = pharmStrings
    val bg = if (result.hasErrors) t.colors.warningBg else t.colors.successBg
    val fg = if (result.hasErrors) t.colors.warningFg else t.colors.successFg
    val title = if (result.hasErrors) s.bulkImportResultPartial else s.bulkImportResultAllSuccess
    val icon = if (result.hasErrors) PharmIcons.Warning else PharmIcons.Check

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = PharmText.h3.copy(color = fg, fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = s.bulkImportResultSummary(result.imported, result.totalAttempted),
                style = PharmText.bodySm.copy(color = fg).tabular(),
            )
        }
    }
}

@Composable
internal fun BulkImportResultHeader(rows: List<BulkImportRow>) {
    val t = pharmTokens
    val s = pharmStrings
    val done = rows.count { it.status == BulkImportRowStatus.Done }
    val failed = rows.count { it.status == BulkImportRowStatus.Failed }
    val pending = rows.count { it.status == BulkImportRowStatus.Pending }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = s.bulkImportResultTitle(rows.size),
            style = PharmText.h3,
            modifier = Modifier.weight(1f),
        )
        if (pending > 0) {
            BulkImportCountChip(label = s.bulkImportStatusReady, value = pending, color = t.colors.warningFg)
        }
        if (done > 0) {
            BulkImportCountChip(label = s.bulkImportResultSuccessLabel, value = done, color = t.colors.successFg)
        }
        if (failed > 0) {
            BulkImportCountChip(label = s.bulkImportStatusError, value = failed, color = t.colors.dangerFg)
        }
    }
}

@Composable
private fun BulkImportCountChip(label: String, value: Int, color: Color) {
    val t = pharmTokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "$value",
            style = PharmText.bodySm.copy(color = color, fontWeight = FontWeight.SemiBold).tabular(),
        )
        Text(text = label, style = PharmText.micro.copy(color = t.colors.fg3))
    }
}
