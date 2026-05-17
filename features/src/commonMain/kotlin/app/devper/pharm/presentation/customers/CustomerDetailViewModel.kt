package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.usecase.GetCustomerSalesUseCase
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.ui.common.BaseViewModel

class CustomerDetailViewModel(
    private val getCustomers: GetCustomersUseCase,
    private val getCustomerSales: GetCustomerSalesUseCase,
) : BaseViewModel<CustomerDetailUiState>(CustomerDetailUiState()) {

    fun load(customerId: String) {
        setState {
            copy(customer = null, sales = emptyList(), customerLoading = true, salesLoading = true)
        }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list ->
                val c = list.firstOrNull { it.id == customerId }
                setState { copy(customerLoading = false, customer = c) }
                if (c == null) setState { copy(error = "ไม่พบลูกค้า") }
            },
            onFailure = { e ->
                setState { copy(customerLoading = false, error = e.message ?: "โหลดข้อมูลลูกค้าไม่สำเร็จ") }
            },
        )
        launchResult(
            block = { getCustomerSales(customerId) },
            onSuccess = { list -> setState { copy(salesLoading = false, sales = list) } },
            onFailure = { e ->
                setState { copy(salesLoading = false, error = e.message ?: "โหลดประวัติการขายไม่สำเร็จ") }
            },
        )
    }

    fun dismissError() = setState { copy(error = null) }
}
