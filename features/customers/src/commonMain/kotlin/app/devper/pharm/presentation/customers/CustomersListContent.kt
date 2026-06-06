package app.devper.pharm.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.pricing.Tier
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomersListContent(
    state: CustomersListUiState,
    callbacks: CustomersListCallbacks = CustomersListCallbacks(),
) {
    val t = pharmTokens
    val visible = state.filtered
    val searching = state.query.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            CustomersListToolbar(query = state.query, callbacks = callbacks)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            PharmListResultLine(
                total = state.customers.size,
                noun = "ราย",
                visible = visible.size,
                searching = searching,
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.customers.isEmpty() -> PharmListSkeleton()
                else -> CustomersListTable(
                    customers = visible,
                    callbacks = callbacks,
                    emptySearching = searching,
                )
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

private val sampleCustomers = listOf(
    Customer(
        id = "1",
        name = "คุณสมศรี ใจดี",
        phone = "081-234-5678",
        priceTier = Tier.Wholesale,
        allergyNote = "แพ้ Penicillin",
    ),
    Customer(
        id = "2",
        name = "นาย วรพล สุขสันต์",
        phone = "089-555-1234",
        priceTier = Tier.Retail,
        allergyNote = null,
    ),
    Customer(
        id = "3",
        name = "นาง พรรณี สวยงาม",
        phone = "092-888-9999",
        priceTier = Tier.Retail,
        allergyNote = "เบาหวาน, ความดัน",
    ),
    Customer(
        id = "4",
        name = "นาย เอกชัย สุภาพ",
        phone = "061-777-2233",
        priceTier = Tier.Retail,
        allergyNote = "หอบหืด",
    ),
    Customer(
        id = "5",
        name = "นาง สุดา สมใจ",
        phone = "080-444-1111",
        priceTier = Tier.Wholesale,
        allergyNote = "แพ้กลิ่นน้ำหอม",
    ),
    Customer(
        id = "6",
        name = "นาย ธีรพงษ์ ใจเย็น",
        phone = "096-321-6789",
        priceTier = Tier.Retail,
        allergyNote = null,
    ),
)

@Preview
@Composable
private fun CustomersListContent_Loaded_Preview() {
    PharmacyTheme {
        CustomersListContent(state = CustomersListUiState(customers = sampleCustomers))
    }
}

@Preview
@Composable
private fun CustomersListContent_Empty_Preview() {
    PharmacyTheme {
        CustomersListContent(state = CustomersListUiState(customers = emptyList()))
    }
}

@Preview
@Composable
private fun CustomersListContent_Loading_Preview() {
    PharmacyTheme {
        CustomersListContent(state = CustomersListUiState(loading = true))
    }
}
