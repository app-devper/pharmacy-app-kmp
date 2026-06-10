package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.CartRepository

class SelectCustomerUseCase(private val cart: CartRepository) : BaseSyncUseCase<Customer, Unit>() {
    override fun execute(param: Customer) = cart.selectCustomer(param)
}
