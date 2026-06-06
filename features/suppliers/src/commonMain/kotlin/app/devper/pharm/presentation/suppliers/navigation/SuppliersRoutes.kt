package app.devper.pharm.presentation.suppliers

import kotlinx.serialization.Serializable

@Serializable data object Suppliers
@Serializable data object SupplierAdd
@Serializable data class SupplierEdit(val id: String)
