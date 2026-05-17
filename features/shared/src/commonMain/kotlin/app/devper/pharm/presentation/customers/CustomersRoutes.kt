package app.devper.pharm.presentation.customers

import kotlinx.serialization.Serializable

@Serializable data object Customers
@Serializable data object CustomerAdd
@Serializable data class CustomerEdit(val id: String)
@Serializable data class CustomerDetail(val id: String)
