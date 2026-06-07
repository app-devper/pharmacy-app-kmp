package app.devper.pharm.domain.param

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.AltUnit

data class AddDrugParam(
    val name: String,
    val genericName: String = "",
    val type: String = "",
    val strength: String = "",
    val barcode: String = "",
    val sellPrice: Money,
    val costPrice: Money = Money.Zero,

    val stock: Quantity = Quantity.Zero,
    val minStock: Quantity = Quantity.Zero,
    val regNo: String = "",
    val unit: String = "ชิ้น",
    val reportTypes: List<String> = emptyList(),
    val altUnits: List<AltUnit> = emptyList(),

    val prices: Map<String, Money> = emptyMap(),
    val createLot: CreateLotPayload? = null,
)

data class UpdateDrugParam(
    val id: String,
    val name: String,
    val genericName: String = "",
    val type: String = "",
    val strength: String = "",
    val barcode: String = "",
    val sellPrice: Money,
    val costPrice: Money = Money.Zero,
    val minStock: Quantity = Quantity.Zero,
    val regNo: String = "",
    val unit: String = "ชิ้น",
    val reportTypes: List<String> = emptyList(),
    val altUnits: List<AltUnit> = emptyList(),
    val prices: Map<String, Money> = emptyMap(),
)

data class CreateLotPayload(
    val lotNumber: String,
    val expiryDate: kotlinx.datetime.LocalDate,
    val importDate: kotlinx.datetime.LocalDate? = null,
    val costPrice: Money? = null,
    val sellPrice: Money? = null,
    val quantity: Quantity,
)
