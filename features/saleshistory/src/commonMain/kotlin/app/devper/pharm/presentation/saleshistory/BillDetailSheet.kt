package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.format.localDateTimeToBuddhist
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailSheet(
    sale: SaleSummary,
    items: List<SaleItemSnapshot>,
    itemsLoading: Boolean,
    onDismiss: () -> Unit,
) {
    val t = pharmTokens
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BillHeader(sale = sale)
            Divider()

            if (itemsLoading && items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) { PharmCircularProgress(color = t.colors.accent) }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items, key = { it.id }) { item -> BillItemRow(item) }
                }
            }

            Divider()
            BillTotals(sale = sale, items = items)

            PharmButton(
                label = "ปิด",
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BillHeader(sale: SaleSummary) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "บิล ${sale.billNo}", style = PharmText.h1)
            if (sale.voided) {
                PharmBadge(text = "ยกเลิกแล้ว", tone = PharmBadgeTone.Red)
            }
        }
        Text(
            text = "${sale.customerName} · ${localDateTimeToBuddhist(sale.soldAt)}",
            style = PharmText.body.copy(color = t.colors.fg2),
        )
    }
}

@Composable
private fun BillItemRow(item: SaleItemSnapshot) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.drugName, style = PharmText.body.copy(color = t.colors.fg1))
            Text(
                text = "${item.qty} ${item.displayUnit} × ${fmtBaht(item.price)}",
                style = PharmText.micro.tabular().copy(color = t.colors.fg3),
            )
            if (item.returnedQty > 0) {
                Text(
                    text = "คืนแล้ว ${item.returnedQty}",
                    style = PharmText.micro.copy(color = t.colors.dangerFg),
                )
            }
        }
        Text(
            text = fmtBaht(item.price * item.qty),
            style = PharmText.body.tabular().copy(color = t.colors.fg1),
        )
    }
}

@Composable
private fun BillTotals(sale: SaleSummary, items: List<SaleItemSnapshot>) {
    val subtotal = items.sumOf { it.price * it.qty }
    TotalRow(label = "รวม", value = subtotal)
    if (sale.discount > 0) {
        TotalRow(label = "ส่วนลด", value = -sale.discount)
    }
    TotalRow(label = "สุทธิ", value = sale.total, emphasize = true)
}

@Composable
private fun TotalRow(label: String, value: Double, emphasize: Boolean = false) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = if (emphasize) {
                PharmText.body.copy(color = t.colors.fg1, fontWeight = FontWeight.SemiBold)
            } else {
                PharmText.bodySm.copy(color = t.colors.fg3)
            },
        )
        Text(
            text = fmtBaht(value),
            style = (if (emphasize) PharmText.body else PharmText.bodySm).copy(
                color = if (emphasize) t.colors.accent else t.colors.fg2,
                fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            ).tabular(),
        )
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
