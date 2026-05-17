package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.AltUnit

data class AddDrugParam(
    val name: String,
    val genericName: String = "",
    val type: String = "",
    val strength: String = "",
    val barcode: String = "",
    val sellPrice: Double,
    val costPrice: Double = 0.0,

    val stock: Int = 0,
    val minStock: Int = 0,
    val regNo: String = "",
    val unit: String = "ชิ้น",
    val reportTypes: List<String> = emptyList(),
    val altUnits: List<AltUnit> = emptyList(),

    val prices: Map<String, Double> = emptyMap(),
    val createLot: CreateLotPayload? = null,
)

data class UpdateDrugParam(
    val id: String,
    val name: String,
    val genericName: String = "",
    val type: String = "",
    val strength: String = "",
    val barcode: String = "",
    val sellPrice: Double,
    val costPrice: Double = 0.0,
    val minStock: Int = 0,
    val regNo: String = "",
    val unit: String = "ชิ้น",
    val reportTypes: List<String> = emptyList(),
    val altUnits: List<AltUnit> = emptyList(),
    val prices: Map<String, Double> = emptyMap(),
)

data class CreateLotPayload(
    val lotNumber: String,
    val expiryDate: String,
    val importDate: String? = null,
    val costPrice: Double? = null,
    val sellPrice: Double? = null,
    val quantity: Int,
)
