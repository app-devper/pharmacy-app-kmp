package app.devper.pharm.domain.param.customers

data class CustomerInput(
    val name: String,
    val phone: String = "",
    val allergyNote: String = "",
    val priceTier: String = "",
)
