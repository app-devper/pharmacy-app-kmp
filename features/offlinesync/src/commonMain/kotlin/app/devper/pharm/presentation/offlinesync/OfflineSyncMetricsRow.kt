package app.devper.pharm.presentation.offlinesync

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun OfflineSyncMetricsRow(pending: List<PendingSale>, modifier: Modifier = Modifier) {
    val s = pharmStrings
    val total = pending.size
    val needsAction = pending.count { it.state != PendingSaleState.Pending }

    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = s.offlineSyncMetricsTotal,
            value = total.toString(),
            sub = s.offlineSyncMetricsLocation,
            tint = if (total > 0) MetricTint.Warning else MetricTint.Neutral,
        )
        MetricCard(
            label = s.offlineSyncMetricsNeedsAction,
            value = needsAction.toString(),
            sub = s.offlineSyncNeedsActionSub,
            tint = if (needsAction > 0) MetricTint.Danger else MetricTint.Neutral,
        )
    }
}
