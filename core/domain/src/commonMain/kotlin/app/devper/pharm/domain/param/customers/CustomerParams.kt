package app.devper.pharm.domain.param

data class AddCustomerParam(
    val name: String,
    val phone: String = "",
    val allergyNote: String = "",
    val priceTier: String = "",
)

data class UpdateCustomerParam(
    val id: String,
    val name: String,
    val phone: String = "",
    val allergyNote: String = "",
    val priceTier: String = "",
)
