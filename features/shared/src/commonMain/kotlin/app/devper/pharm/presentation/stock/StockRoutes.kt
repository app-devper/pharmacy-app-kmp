package app.devper.pharm.presentation.stock

import kotlinx.serialization.Serializable

@Serializable data object Stock
@Serializable data object DrugAdd
@Serializable data class DrugEdit(val id: String)
@Serializable data class DrugHistory(val drugId: String, val drugName: String)
