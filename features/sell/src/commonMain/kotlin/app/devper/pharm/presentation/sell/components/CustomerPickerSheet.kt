package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.pricing.tierLabel
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPickerSheet(
    customers: List<Customer>,
    loading: Boolean,
    onPick: (Customer) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    val filtered = remember(customers, query) {
        if (query.isBlank()) customers
        else customers.filter { c ->
            c.name.contains(query, ignoreCase = true) ||
                (c.phone?.contains(query) == true)
        }
    }

    val t = pharmTokens
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(modifier = Modifier.fillMaxHeight(0.85f)) {
            Text(
                text = "เลือกลูกค้า",
                style = PharmText.h2,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                PharmTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "ค้นหาชื่อ / เบอร์โทร",
                )
            }

            Box(modifier = Modifier.fillMaxHeight()) {
                val hasQuery = query.isNotBlank()
                when {
                    loading && customers.isEmpty() ->
                        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                            PharmCircularProgress(color = t.colors.accent)
                        }
                    filtered.isEmpty() -> EmptyCustomers(searching = hasQuery)
                    else -> LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
                        items(filtered, key = { it.id }) { customer ->
                            CustomerRow(customer, onClick = { onPick(customer) })
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(t.colors.borderSubtle),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerRow(customer: Customer, onClick: () -> Unit) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = customer.name,
            style = PharmText.body.copy(fontWeight = FontWeight.SemiBold),
        )
        customer.phone?.let {
            Text(text = it, style = PharmText.meta)
        }
        if (customer.priceTier.isNotBlank() && customer.priceTier != "retail") {
            Box(modifier = Modifier.padding(top = 4.dp)) {
                PharmBadge(
                    text = tierLabel(customer.priceTier),
                    tone = PharmBadgeTone.Indigo,
                    size = PharmBadgeSize.Sm,
                )
            }
        }
    }
}

@Composable
private fun EmptyCustomers(searching: Boolean) {
    val t = pharmTokens
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = if (searching) PharmIcons.Search else PharmIcons.Customers,
            contentDescription = null,
            tint = t.colors.fgMuted,
            modifier = Modifier.size(36.dp),
        )
        Text(
            text = if (searching) "ไม่พบลูกค้าที่ค้นหา" else "ยังไม่มีรายชื่อลูกค้า",
            style = PharmText.body.copy(color = t.colors.fg3),
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
