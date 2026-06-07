package app.devper.pharm.domain.validation

import app.devper.pharm.common.ValidationException
import kotlinx.datetime.LocalDate

object Field {

    fun notBlank(value: String, label: String): String {
        if (value.isBlank()) throw ValidationException("ต้องระบุ$label")
        return value.trim()
    }

    fun localDate(value: String, label: String = "วันที่"): LocalDate {
        val trimmed = notBlank(value, label)
        return runCatching { LocalDate.parse(trimmed) }.getOrNull()
            ?: throw ValidationException("${label}ไม่ถูกต้อง (รูปแบบ YYYY-MM-DD)")
    }

    fun positiveInt(value: String, label: String = "จำนวน"): Int {
        val parsed = value.toIntOrNull()
            ?: throw ValidationException("${label}ต้องเป็นตัวเลข")
        if (parsed <= 0) throw ValidationException("${label}ต้องมากกว่า 0")
        return parsed
    }

    fun nonNegativeIntOrDefault(value: String, default: Int = 0, label: String = "ยอด"): Int {
        if (value.isBlank()) return default
        val parsed = value.toIntOrNull()
            ?: throw ValidationException("${label}ต้องเป็นตัวเลข")
        if (parsed < 0) throw ValidationException("${label}ต้องไม่ติดลบ")
        return parsed
    }

    fun nonNegativeDouble(value: String, label: String): Double {
        val parsed = value.toDoubleOrNull()
            ?: throw ValidationException("${label}ต้องเป็นตัวเลข")
        if (parsed < 0.0) throw ValidationException("${label}ต้องไม่ติดลบ")
        return parsed
    }

    fun nonNegativeDoubleOrDefault(value: String, default: Double = 0.0, label: String = "มูลค่า"): Double {
        if (value.isBlank()) return default
        val parsed = value.toDoubleOrNull()
            ?: throw ValidationException("${label}ต้องเป็นตัวเลข")
        if (parsed < 0.0) throw ValidationException("${label}ต้องไม่ติดลบ")
        return parsed
    }
}

object Check {

    fun notBlank(value: String): Boolean = value.isNotBlank()

    fun localDate(value: String): Boolean =
        value.isNotBlank() && runCatching { LocalDate.parse(value.trim()) }.isSuccess

    fun positiveInt(value: String): Boolean = (value.toIntOrNull() ?: 0) > 0

    fun nonNegativeDouble(value: String): Boolean = (value.toDoubleOrNull() ?: -1.0) >= 0.0
}
