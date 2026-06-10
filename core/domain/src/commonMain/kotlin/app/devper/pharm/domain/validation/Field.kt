package app.devper.pharm.domain.validation

import kotlinx.datetime.LocalDate

object Field {

    fun notBlank(value: String, label: FieldLabel): String {
        if (value.isBlank()) throw FieldValidationError.Required(label)
        return value.trim()
    }

    fun localDate(value: String, label: FieldLabel = FieldLabel.Date): LocalDate {
        val trimmed = notBlank(value, label)
        return runCatching { LocalDate.parse(trimmed) }.getOrNull()
            ?: throw FieldValidationError.InvalidDate(label)
    }

    fun positiveInt(value: String, label: FieldLabel = FieldLabel.Quantity): Int {
        val parsed = value.toIntOrNull()
            ?: throw FieldValidationError.NotANumber(label)
        if (parsed <= 0) throw FieldValidationError.MustBePositive(label)
        return parsed
    }

    fun nonNegativeIntOrDefault(value: String, default: Int = 0, label: FieldLabel = FieldLabel.Amount): Int {
        if (value.isBlank()) return default
        val parsed = value.toIntOrNull()
            ?: throw FieldValidationError.NotANumber(label)
        if (parsed < 0) throw FieldValidationError.MustBeNonNegative(label)
        return parsed
    }

    fun nonNegativeDouble(value: String, label: FieldLabel): Double {
        val parsed = value.toDoubleOrNull()
            ?: throw FieldValidationError.NotANumber(label)
        if (parsed < 0.0) throw FieldValidationError.MustBeNonNegative(label)
        return parsed
    }

    fun nonNegativeDoubleOrDefault(value: String, default: Double = 0.0, label: FieldLabel = FieldLabel.Value): Double {
        if (value.isBlank()) return default
        val parsed = value.toDoubleOrNull()
            ?: throw FieldValidationError.NotANumber(label)
        if (parsed < 0.0) throw FieldValidationError.MustBeNonNegative(label)
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
