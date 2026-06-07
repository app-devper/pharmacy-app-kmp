package app.devper.pharm.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.extension.tierLabel
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.format.localDateTimeToBuddhist
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomerDetailContent(
    state: CustomerDetailUiState,
    callbacks: CustomerDetailCallbacks = CustomerDetailCallbacks(),
) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = state.customer?.name ?: "ลูกค้า",
            onBack = callbacks.onBack,
            actions = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(t.shapes.md)
                        .clickable(role = Role.Button, onClick = callbacks.onEdit),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        PharmIcons.Pencil,
                        contentDescription = "แก้ไข",
                        tint = t.colors.fg2,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
        )
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            CustomerHeader(customer = state.customer, loading = state.customerLoading)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            SalesSection(state = state)
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun CustomerHeader(customer: Customer?, loading: Boolean) {
    val t = pharmTokens
    if (loading && customer == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            PharmCircularProgress()
        }
        return
    }
    customer ?: return

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(t.colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    PharmIcons.Person,
                    contentDescription = null,
                    tint = t.colors.surface,
                    modifier = Modifier.size(28.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = customer.name, style = PharmText.h1)
                Text(
                    text = customer.phone?.takeIf { it.isNotBlank() } ?: "ไม่ระบุเบอร์โทร",
                    style = PharmText.body.copy(color = t.colors.fg2),
                )
            }
            if (customer.priceTier.isNotBlank() && customer.priceTier != Tier.Retail) {
                PharmBadge(text = tierLabel(customer.priceTier), tone = PharmBadgeTone.Purple)
            }
        }

        customer.allergyNote?.takeIf { it.isNotBlank() }?.let { note ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(t.shapes.md)
                    .background(t.colors.warningBg, t.shapes.md)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.md),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        PharmIcons.Warning,
                        contentDescription = null,
                        tint = t.colors.warningFg,
                        modifier = Modifier.size(18.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "แพ้ยา / โรคประจำตัว",
                            style = PharmText.h3.copy(color = t.colors.warningFg),
                        )
                        Text(text = note, style = PharmText.bodySm.copy(color = t.colors.warningFg))
                    }
                }
            }
        }
    }
}

@Composable
private fun SalesSection(state: CustomerDetailUiState) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "ประวัติการขาย",
            style = PharmText.h2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
        when {
            state.salesLoading && state.sales.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    PharmCircularProgress()
                }
            }
            state.sales.isEmpty() -> {
                Text(
                    text = "ลูกค้ารายนี้ยังไม่มีบิล",
                    style = PharmText.body.copy(color = t.colors.fg2),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.sales, key = { it.id }) { sale ->
                        SaleRow(sale)
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
                    }
                }
            }
        }
    }
}

@Composable
private fun SaleRow(sale: SaleSummary) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = sale.billNo.ifBlank { "(ไม่มีเลขบิล)" },
                    style = PharmText.h3.tabular(),
                )
                if (sale.voided) {
                    PharmBadge(text = "ยกเลิกแล้ว", tone = PharmBadgeTone.Red)
                }
            }
            Text(
                text = localDateTimeToBuddhist(sale.soldAt),
                style = PharmText.micro.tabular().copy(color = t.colors.fg2),
            )
        }
        Text(
            text = formatBahtCurrency(sale.total),
            style = PharmText.h2.tabular().copy(color = t.colors.accent),
        )
    }
}

private val sampleCustomer = Customer(
    id = "c1",
    name = "สมศรี ใจดี",
    phone = "0812345678",
    priceTier = Tier.Wholesale,
    allergyNote = "แพ้ Penicillin",
)

private val sampleSales = listOf(
    SaleSummary(
        id = "s1",
        billNo = "INV-260601-001",
        customerName = "สมศรี ใจดี",
        total = 480.0,
        discount = 0.0,
        soldAt = kotlinx.datetime.LocalDateTime.parse("2026-06-01T10:30:00"),
        voided = false,
    ),
    SaleSummary(
        id = "s2",
        billNo = "INV-260528-014",
        customerName = "สมศรี ใจดี",
        total = 120.0,
        discount = 20.0,
        soldAt = kotlinx.datetime.LocalDateTime.parse("2026-05-28T16:05:00"),
        voided = true,
    ),
)

@Preview
@Composable
private fun CustomerDetailContent_Loaded_Preview() {
    PharmacyTheme {
        CustomerDetailContent(
            state = CustomerDetailUiState(
                customer = sampleCustomer,
                sales = sampleSales,
            ),
        )
    }
}

@Preview
@Composable
private fun CustomerDetailContent_Loading_Preview() {
    PharmacyTheme {
        CustomerDetailContent(
            state = CustomerDetailUiState(customerLoading = true, salesLoading = true),
        )
    }
}

@Preview
@Composable
private fun CustomerDetailContent_EmptySales_Preview() {
    PharmacyTheme {
        CustomerDetailContent(
            state = CustomerDetailUiState(customer = sampleCustomer.copy(allergyNote = null)),
        )
    }
}
