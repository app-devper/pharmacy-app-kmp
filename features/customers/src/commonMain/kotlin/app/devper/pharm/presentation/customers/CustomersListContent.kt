package app.devper.pharm.presentation.customers

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.presentation.customers.i18n.localizeCustomersList
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.components.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme

@Composable
fun CustomersListContent(
    state: CustomersListUiState,
    callbacks: CustomersListCallbacks = CustomersListCallbacks(),
) {
    val pageIsEmpty = state.customers.isEmpty()
    val s = pharmStrings
    val visible = state.filtered
    val searching = state.query.isNotBlank()

    PharmListScaffold(
        toolbar = { CustomersListToolbar(query = state.query, callbacks = callbacks) },
        resultLine = {
            PharmListResultLine(
                total = state.customers.size,
                noun = s.customersCountNoun,
                visible = visible.size,
                searching = searching,
            )
        },
    ) {
        when {
            state.loading && pageIsEmpty -> PharmListSkeleton(modifier = Modifier.fillMaxSize())
            state.errorState != null && pageIsEmpty ->
                PharmErrorState()
            pageIsEmpty -> PharmEmptyState(
                icon = PharmIcons.Customers,
                title = s.customersListEmpty,
                action = {
                    PharmButton(
                        label = s.customersAddCta,
                        onClick = callbacks.onOpenAdd,
                        size = PharmButtonSize.Sm,
                    )
                },
            )
            else -> CustomersListTable(
                customers = visible,
                callbacks = callbacks,
                emptySearching = searching,
            )
        }
    }

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(pageIsEmpty)?.localizeCustomersList(s), onDismiss = callbacks.onDismissError)
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
