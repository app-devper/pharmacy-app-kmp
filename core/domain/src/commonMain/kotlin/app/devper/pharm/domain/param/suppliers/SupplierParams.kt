package app.devper.pharm.domain.param.suppliers

data class SupplierInput(
    val name: String,
    val contactName: String = "",
    val phone: String = "",
    val address: String = "",
    val taxId: String = "",
    val notes: String = "",
)

data class UpdateSupplierParam(
    val id: String,
    val input: SupplierInput,
)
