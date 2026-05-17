package app.devper.pharm.domain.model

data class Supplier(
    val id: String,
    val name: String,
    val contactName: String,
    val phone: String,
    val address: String,
    val taxId: String,
    val notes: String,
)
