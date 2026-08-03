package app.devper.pharm.presentation.imports

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
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmDivider
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ImportDetailActionBar(
    po: PurchaseOrder,
    state: ImportDetailUiState,
    callbacks: ImportDetailCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    Column(modifier = Modifier.fillMaxWidth().background(t.colors.surface)) {
        PharmDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (po.status == PurchaseOrderStatus.Draft) {
                PharmButton(
                    label = s.commonDelete,
                    onClick = callbacks.onAskDelete,
                    variant = PharmButtonVariant.Outline,
                    enabled = !state.confirming && !state.deleting,
                    loading = state.deleting,
                    leadingIcon = {
                        Icon(PharmIcons.Trash, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
                PharmButton(
                    label = s.importsConfirmReceiveCta,
                    onClick = callbacks.onAskConfirm,
                    modifier = Modifier.weight(1f),
                    enabled = !state.confirming && !state.deleting && po.itemCount > 0,
                    loading = state.confirming,
                    leadingIcon = {
                        Icon(PharmIcons.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
            } else {
                Text(
                    text = s.importsStatusReceivedDetail,
                    style = PharmText.h3.copy(color = t.colors.successFg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(t.shapes.md)
                        .background(t.colors.successBg, t.shapes.md)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }
        }
    }
}
