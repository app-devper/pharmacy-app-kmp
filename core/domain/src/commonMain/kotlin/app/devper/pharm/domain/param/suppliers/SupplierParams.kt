package app.devper.pharm.domain.param

data class AddSupplierParam(
    val name: String,
    val contactName: String = "",
    val phone: String = "",
    val address: String = "",
    val taxId: String = "",
    val notes: String = "",
)

data class UpdateSupplierParam(
    val id: String,
    val name: String,
    val contactName: String = "",
    val phone: String = "",
    val address: String = "",
    val taxId: String = "",
    val notes: String = "",
)
