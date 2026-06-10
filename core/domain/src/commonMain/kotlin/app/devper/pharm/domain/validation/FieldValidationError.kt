package app.devper.pharm.domain.validation

import app.devper.pharm.common.AppException

enum class FieldLabel {
    Date,
    Quantity,
    Amount,
    Value,
    Drug,
    DrugName,
    LotNumber,
    ExpiryDate,
    Unit,
    PricePerUnit,
    Balance,
    TotalValue,
}

sealed class FieldValidationError(val field: FieldLabel, rule: String) : AppException("validation.$rule:$field") {
    class Required(field: FieldLabel) : FieldValidationError(field, "required")
    class InvalidDate(field: FieldLabel) : FieldValidationError(field, "invalid_date")
    class NotANumber(field: FieldLabel) : FieldValidationError(field, "not_a_number")
    class MustBePositive(field: FieldLabel) : FieldValidationError(field, "must_be_positive")
    class MustBeNonNegative(field: FieldLabel) : FieldValidationError(field, "must_be_non_negative")
}
