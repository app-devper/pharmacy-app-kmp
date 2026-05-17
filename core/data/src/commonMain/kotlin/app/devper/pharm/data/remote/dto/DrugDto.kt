package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrugDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("generic_name") val genericName: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("strength") val strength: String? = null,
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("sell_price") val sellPrice: Double = 0.0,
    @SerialName("cost_price") val costPrice: Double = 0.0,
    @SerialName("stock") val stock: Int = 0,
    @SerialName("min_stock") val minStock: Int = 0,
    @SerialName("unit") val unit: String? = null,
    @SerialName("reg_no") val regNo: String? = null,
    @SerialName("prices") val prices: Map<String, Double>? = null,
    @SerialName("alt_units") val altUnits: List<AltUnitDto>? = null,
    @SerialName("report_types") val reportTypes: List<String>? = null,
)

@Serializable
data class AltUnitDto(
    @SerialName("name") val name: String,
    @SerialName("factor") val factor: Int,
    @SerialName("sell_price") val sellPrice: Double = 0.0,
    @SerialName("prices") val prices: Map<String, Double>? = null,
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("hidden") val hidden: Boolean = false,
)

@Serializable
data class DrugInputDto(
    @SerialName("name") val name: String,
    @SerialName("generic_name") val genericName: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("strength") val strength: String = "",
    @SerialName("barcode") val barcode: String = "",
    @SerialName("sell_price") val sellPrice: Double,
    @SerialName("cost_price") val costPrice: Double = 0.0,
    @SerialName("stock") val stock: Int = 0,
    @SerialName("min_stock") val minStock: Int = 0,
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("unit") val unit: String = "ชิ้น",
    @SerialName("report_types") val reportTypes: List<String> = emptyList(),
    @SerialName("alt_units") val altUnits: List<AltUnitDto> = emptyList(),
    @SerialName("prices") val prices: Map<String, Double> = emptyMap(),
    @SerialName("create_lot") val createLot: CreateLotDto? = null,
)

@Serializable
data class DrugUpdateDto(
    @SerialName("name") val name: String,
    @SerialName("generic_name") val genericName: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("strength") val strength: String = "",
    @SerialName("barcode") val barcode: String = "",
    @SerialName("sell_price") val sellPrice: Double,
    @SerialName("cost_price") val costPrice: Double = 0.0,
    @SerialName("min_stock") val minStock: Int = 0,
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("unit") val unit: String = "ชิ้น",
    @SerialName("report_types") val reportTypes: List<String> = emptyList(),
    @SerialName("alt_units") val altUnits: List<AltUnitDto> = emptyList(),
    @SerialName("prices") val prices: Map<String, Double> = emptyMap(),
)

@Serializable
data class CreateLotDto(
    @SerialName("lot_number") val lotNumber: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("import_date") val importDate: String? = null,
    @SerialName("cost_price") val costPrice: Double? = null,
    @SerialName("sell_price") val sellPrice: Double? = null,
    @SerialName("quantity") val quantity: Int,
)

@Serializable
data class BulkDrugImportInputDto(
    @SerialName("drugs") val drugs: List<DrugInputDto>,
)

@Serializable
data class BulkImportResultDto(
    @SerialName("imported") val imported: Int = 0,
    @SerialName("errors") val errors: List<BulkImportRowErrorDto> = emptyList(),
)

@Serializable
data class BulkImportRowErrorDto(
    @SerialName("row") val row: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("message") val message: String = "",
)

@Serializable
data class ReorderSuggestionDto(
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("unit") val unit: String = "",
    @SerialName("current_stock") val currentStock: Int = 0,
    @SerialName("min_stock") val minStock: Int = 0,
    @SerialName("qty_sold") val qtySold: Int = 0,
    @SerialName("avg_daily_sale") val avgDailySale: Double = 0.0,
    @SerialName("days_left") val daysLeft: Double = 0.0,
    @SerialName("suggested_qty") val suggestedQty: Int = 0,
    @SerialName("cost_price") val costPrice: Double = 0.0,
    @SerialName("sell_price") val sellPrice: Double = 0.0,
)
