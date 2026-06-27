package app.devper.pharm.presentation.imports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStamp
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.format.formatBaht
import app.devper.pharm.ui.format.localDateToBuddhist
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun StatusChip(status: PurchaseOrderStatus) {
    val pharmStatus = when (status) {
        PurchaseOrderStatus.Draft     -> PharmStatus.Draft
        PurchaseOrderStatus.Confirmed -> PharmStatus.Confirmed
    }
    PharmStatusBadge(status = pharmStatus)
}

@Composable
internal fun DetailRow(label: String, value: String) {
    val t = pharmTokens
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = PharmText.h3.copy(color = t.colors.fg2),
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = value,
            style = PharmText.body,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun ImportDetailItemRow(item: PurchaseOrderItem) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.drugName.ifBlank { pharmStrings.commonNoDrugName },
                style = PharmText.body,
            )
            PharmStamp(
                text = pharmStrings.importsFormItemLotLine(item.lotNumber, localDateToBuddhist(item.expiryDate)),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = pharmStrings.importsQtyPieces(item.qty.value),
                style = PharmText.h3.tabular(),
            )
            Text(
                text = "@${formatBaht(item.costPrice.amount)}",
                style = PharmText.bodySm.tabular().copy(color = t.colors.fg2),
            )
        }
    }
}
