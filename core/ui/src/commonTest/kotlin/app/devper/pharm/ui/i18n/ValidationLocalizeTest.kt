package app.devper.pharm.ui.i18n

import app.devper.pharm.domain.validation.FieldLabel
import app.devper.pharm.domain.validation.FieldValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ValidationLocalizeTest {

    @Test
    fun required_interpolates_thai_field_label() {
        val text = FieldValidationError.Required(FieldLabel.DrugName).localizeCommon(PharmStringsTh)
        assertEquals("ต้องระบุชื่อยา", text)
    }

    @Test
    fun invalid_date_interpolates_expiry_label() {
        val text = FieldValidationError.InvalidDate(FieldLabel.ExpiryDate).localizeCommon(PharmStringsTh)
        assertEquals("วันหมดอายุไม่ถูกต้อง (รูปแบบ YYYY-MM-DD)", text)
    }

    @Test
    fun english_differs_from_thai() {
        val err = FieldValidationError.MustBePositive(FieldLabel.Quantity)
        assertNotEquals(err.localizeCommon(PharmStringsTh), err.localizeCommon(PharmStringsEn))
        assertEquals("Quantity must be greater than 0", err.localizeCommon(PharmStringsEn))
    }
}
