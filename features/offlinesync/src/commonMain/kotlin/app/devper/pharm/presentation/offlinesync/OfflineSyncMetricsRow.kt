package app.devper.pharm.presentation.offlinesync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricTint

@Composable
internal fun OfflineSyncMetricsRow(pending: List<PendingSale>, modifier: Modifier = Modifier) {
    val total = pending.size
    val failed = pending.count { it.lastError != null }
    val attempts = pending.sumOf { it.attempts }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MetricCard(
            label = "รายการค้างทั้งหมด",
            value = total.toString(),
            sub = "ใน IndexedDB",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "ซิงก์ล้มเหลว",
            value = failed.toString(),
            sub = "รอ retry",
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "ความพยายามสะสม",
            value = attempts.toString(),
            sub = "ครั้งสะสม",
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
    }
}
