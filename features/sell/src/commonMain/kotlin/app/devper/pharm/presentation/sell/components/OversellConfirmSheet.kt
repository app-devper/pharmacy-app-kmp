package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.OversellShortfall
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.common.pharmToggleable
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmBottomSheet
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCheckbox
import app.devper.pharm.ui.designsystem.PharmHelpHint
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun OversellConfirmSheet(
    shortfalls: List<OversellShortfall>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    var ack by remember(shortfalls) { mutableStateOf(false) }
    val t = pharmTokens

    PharmBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(t.shapes.md)
                        .background(t.colors.dangerBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = PharmIcons.Warning,
                        contentDescription = null,
                        tint = t.colors.dangerFg,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        pharmStrings.sellShortfall,
                        style = PharmText.h2,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
                    )
                    Text(pharmStrings.sellOversellCount(shortfalls.size), style = PharmText.meta)
                }
                PharmHelpHint(
                    text = pharmStrings.sellOversellExplain,
                )
            }

            Divider()

            shortfalls.forEach { row -> ShortfallRow(row) }

            Divider()

            Text(
                pharmStrings.sellOversellReconcileNote,
                style = PharmText.micro,
            )

            Row(
                modifier = Modifier.pharmToggleable(
                    value = ack,
                    role = Role.Checkbox,
                    shape = pharmTokens.shapes.md,
                    onValueChange = { ack = it },
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PharmCheckbox(checked = ack, onCheckedChange = null)
                Text(pharmStrings.sellOversellConfirmCheck, style = PharmText.body)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PharmButton(
                    label = pharmStrings.commonCancel,
                    onClick = onDismiss,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
                PharmButton(
                    label = pharmStrings.sellOversold,
                    onClick = onConfirm,
                    variant = PharmButtonVariant.Danger,
                    size = PharmButtonSize.Md,
                    enabled = ack,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ShortfallRow(row: OversellShortfall) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.drugName,
                style = PharmText.bodySm.copy(
                    color = t.colors.fg1,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Text(
                text = pharmStrings.sellOversellNeedHave(row.asked, row.available),
                style = PharmText.micro,
            )
        }
        PharmBadge(text = pharmStrings.sellOversellShortBadge(row.shortfall), tone = PharmBadgeTone.Red, size = PharmBadgeSize.Sm)
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(pharmTokens.colors.divider),
    )
}
