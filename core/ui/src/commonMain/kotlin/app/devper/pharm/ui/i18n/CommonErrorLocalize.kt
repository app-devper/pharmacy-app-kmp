package app.devper.pharm.ui.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.ServerException
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.common.ValidationException
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.validation.FieldLabel
import app.devper.pharm.domain.validation.FieldValidationError

fun AppException.localizeCommon(s: PharmStrings): String = when (this) {
    is FieldValidationError -> localizeFieldValidation(s)
    is CommonUiStateError.LoadFailed -> s.commonLoadFailed
    is CommonUiStateError.SaveFailed -> s.commonSaveFailed
    is CommonUiStateError.DeleteFailed -> s.commonDeleteFailed
    is CommonUiStateError.ExportFailed -> s.commonExportFailed
    is AuthException -> s.commonErrorAuth
    is ForbiddenException -> s.commonErrorForbidden
    is NotFoundException -> s.commonErrorNotFound
    is ConflictException -> s.commonErrorConflict
    is NetworkException -> s.commonErrorNetwork
    is ServerException -> s.commonErrorServer
    is ValidationException -> message ?: s.commonErrorValidation
    is StorageException -> s.commonErrorStorage
    is UnsupportedPlatformException -> s.commonErrorUnsupported
    else -> s.commonErrorGeneric
}

fun CommonUiStateMessage.localize(s: PharmStrings): String = when (this) {
    is CommonUiStateMessage.ExportEmpty -> s.commonExportEmpty
    is CommonUiStateMessage.Saved -> s.commonSaved
}

fun FieldValidationError.localizeFieldValidation(s: PharmStrings): String {
    val label = s.fieldLabel(field)
    return when (this) {
        is FieldValidationError.Required -> s.validationRequired(label)
        is FieldValidationError.InvalidDate -> s.validationInvalidDate(label)
        is FieldValidationError.NotANumber -> s.validationNotANumber(label)
        is FieldValidationError.MustBePositive -> s.validationMustBePositive(label)
        is FieldValidationError.MustBeNonNegative -> s.validationMustBeNonNegative(label)
    }
}

private fun PharmStrings.fieldLabel(field: FieldLabel): String = when (field) {
    FieldLabel.Date -> fieldDate
    FieldLabel.Quantity -> fieldQuantity
    FieldLabel.Amount -> fieldAmount
    FieldLabel.Value -> fieldValue
    FieldLabel.Drug -> fieldDrug
    FieldLabel.DrugName -> fieldDrugName
    FieldLabel.LotNumber -> fieldLotNumber
    FieldLabel.ExpiryDate -> fieldExpiryDate
    FieldLabel.Unit -> fieldUnit
    FieldLabel.PricePerUnit -> fieldPricePerUnit
    FieldLabel.Balance -> fieldBalance
    FieldLabel.TotalValue -> fieldTotalValue
}
