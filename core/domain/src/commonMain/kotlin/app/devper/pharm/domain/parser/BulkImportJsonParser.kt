package app.devper.pharm.domain.parser

import app.devper.pharm.common.ValidationException
import app.devper.pharm.domain.param.AddDrugParam
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray

class BulkImportJsonParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parse(raw: String): Result<List<AddDrugParam>> = runCatching {
        val trimmed = raw.trim()
        require(trimmed.isNotEmpty()) { "วาง JSON ก่อนตรวจสอบ" }
        val element = json.parseToJsonElement(trimmed)
        val array: JsonArray = when (element) {
            is JsonArray  -> element
            is JsonObject -> element["drugs"]?.takeIf { it is JsonArray }?.jsonArray
                ?: throw ValidationException("ต้องเป็น array หรือ {drugs: [...]}")
            else -> throw ValidationException("รูปแบบ JSON ไม่ถูกต้อง")
        }
        array.mapIndexed { idx, raw ->
            (raw as? JsonObject)?.toAddDrugParam()
                ?: throw ValidationException("รายการที่ ${idx + 1}: ต้องเป็น JSON object")
        }
    }

    private fun JsonObject.toAddDrugParam(): AddDrugParam {
        val name = stringField("name")?.trim().orEmpty()
        require(name.isNotEmpty()) { "ทุกแถวต้องมีฟิลด์ name" }
        return AddDrugParam(
            name = name,
            genericName = stringField("generic_name").orEmpty(),
            type = stringField("type").orEmpty(),
            strength = stringField("strength").orEmpty(),
            barcode = stringField("barcode").orEmpty(),
            sellPrice = doubleField("sell_price") ?: 0.0,
            costPrice = doubleField("cost_price") ?: 0.0,
            stock = intField("stock") ?: 0,
            minStock = intField("min_stock") ?: 0,
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
}
