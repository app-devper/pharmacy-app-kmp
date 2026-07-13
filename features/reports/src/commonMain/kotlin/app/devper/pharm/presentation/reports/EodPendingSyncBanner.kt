package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun EodPendingSyncBanner(count: Int, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.dangerBg)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = PharmIcons.OfflineSync,
            contentDescription = null,
            tint = t.colors.dangerFg,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = pharmStrings.reportsEodPendingSyncBanner(count),
            style = PharmText.meta.copy(color = t.colors.dangerFg, fontWeight = FontWeight.SemiBold),
        )
    }
}
