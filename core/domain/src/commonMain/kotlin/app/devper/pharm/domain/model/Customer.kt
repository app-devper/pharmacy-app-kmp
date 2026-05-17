package app.devper.pharm.domain.model

data class Customer(
    val id: String,
    val name: String,
    val phone: String?,
    val priceTier: String,
    val allergyNote: String?,
)
