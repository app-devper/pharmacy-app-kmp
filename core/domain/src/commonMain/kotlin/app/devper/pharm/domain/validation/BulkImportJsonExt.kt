package app.devper.pharm.domain.validation

import app.devper.pharm.domain.validation.BulkImportParseError
import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.param.inventory.AddDrugParam
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray

private val bulkImportJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun parseBulkImportJson(raw: String): Result<List<AddDrugParam>> = runCatching {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) throw BulkImportParseError.EmptyInput()
    val element = bulkImportJson.parseToJsonElement(trimmed)
    val array: JsonArray = when (element) {
        is JsonArray  -> element
        is JsonObject -> element["drugs"]?.takeIf { it is JsonArray }?.jsonArray
            ?: throw BulkImportParseError.NotArrayOrObject()
        else -> throw BulkImportParseError.NotArrayOrObject()
    }
    array.mapIndexed { idx, raw ->
        (raw as? JsonObject)?.toAddDrugParam()
            ?: throw BulkImportParseError.RowNotObject(idx + 1)
    }
}

private fun JsonObject.toAddDrugParam(): AddDrugParam {
    val name = stringField("name")?.trim().orEmpty()
    if (name.isEmpty()) throw BulkImportParseError.RowMissingName()
    return AddDrugParam(
        name = name,
        genericName = stringField("generic_name").orEmpty(),
        type = stringField("type").orEmpty(),
        strength = stringField("strength").orEmpty(),
        barcode = stringField("barcode").orEmpty(),
        sellPrice = Money(doubleField("sell_price") ?: 0.0),
        costPrice = Money(doubleField("cost_price") ?: 0.0),
        stock = Quantity(intField("stock") ?: 0),
        minStock = Quantity(intField("min_stock") ?: 0),
        regNo = stringField("reg_no").orEmpty(),
        unit = stringField("unit").orEmpty().ifBlank { "ชิ้น" },
        reportTypes = stringArrayField("report_types"),
    )
}

private fun JsonObject.stringField(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }

private fun JsonObject.doubleField(key: String): Double? =
    (this[key] as? JsonPrimitive)?.let { it.doubleOrNull ?: it.contentOrNull?.toDoubleOrNull() }

private fun JsonObject.intField(key: String): Int? =
    (this[key] as? JsonPrimitive)?.let { it.intOrNull ?: it.contentOrNull?.toIntOrNull() }

private fun JsonObject.stringArrayField(key: String): List<String> =
    (this[key] as? JsonArray)?.mapNotNull {
        (it as? JsonPrimitive)?.contentOrNull?.trim()?.takeIf { s -> s.isNotEmpty() }
    } ?: emptyList()
