package app.devper.pharm.domain.param

data class CustomerInput(
    val name: String,
    val phone: String = "",
    val allergyNote: String = "",
    val priceTier: String = "",
)

data class UpdateCustomerParam(
    val id: String,
    val input: CustomerInput,
)
