package app.devper.pharm.domain.validation

import kotlin.test.assertIs
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FieldTest {

    @Test
    fun notBlank_returns_trimmed_value() {
        assertEquals("hello", Field.notBlank("  hello  ", FieldLabel.DrugName))
    }

    @Test
    fun notBlank_throws_typed_ValidationException_with_thai_message() {
        val e = assertFailsWith<FieldValidationError> { Field.notBlank("", FieldLabel.DrugName) }
        assertIs<FieldValidationError.Required>(e)
        assertEquals(FieldLabel.DrugName, e.field)
    }

    @Test
    fun notBlank_throws_for_whitespace_only() {
        assertFailsWith<FieldValidationError> { Field.notBlank("   ", FieldLabel.LotNumber) }
    }

    @Test
    fun localDate_parses_iso_yyyy_mm_dd() {
        assertEquals(LocalDate(2026, 6, 7), Field.localDate("2026-06-07"))
    }

    @Test
    fun localDate_trims_before_parse() {
        assertEquals(LocalDate(2026, 6, 7), Field.localDate("  2026-06-07  "))
    }

    @Test
    fun localDate_throws_for_thai_dd_mm_yyyy() {
        val e = assertFailsWith<FieldValidationError> { Field.localDate("07/06/2026", label = FieldLabel.ExpiryDate) }
        assertIs<FieldValidationError.InvalidDate>(e)
        assertEquals(FieldLabel.ExpiryDate, e.field)
    }

    @Test
    fun localDate_throws_for_blank_with_label() {
        val e = assertFailsWith<FieldValidationError> { Field.localDate("", label = FieldLabel.Date) }
        assertIs<FieldValidationError.Required>(e)
    }

    @Test
    fun positiveInt_parses_value_greater_than_zero() {
        assertEquals(10, Field.positiveInt("10"))
    }

    @Test
    fun positiveInt_throws_for_zero_with_default_label() {
        val e = assertFailsWith<FieldValidationError> { Field.positiveInt("0") }
        assertIs<FieldValidationError.MustBePositive>(e)
        assertEquals(FieldLabel.Quantity, e.field)
    }

    @Test
    fun positiveInt_throws_for_non_numeric() {
        val e = assertFailsWith<FieldValidationError> { Field.positiveInt("abc") }
        assertIs<FieldValidationError.NotANumber>(e)
    }

    @Test
    fun positiveInt_throws_for_negative() {
        assertFailsWith<FieldValidationError> { Field.positiveInt("-5") }
    }

    @Test
    fun nonNegativeIntOrDefault_returns_default_for_blank() {
        assertEquals(0, Field.nonNegativeIntOrDefault("", default = 0))
        assertEquals(100, Field.nonNegativeIntOrDefault("", default = 100))
    }

    @Test
    fun nonNegativeIntOrDefault_parses_valid_value() {
        assertEquals(42, Field.nonNegativeIntOrDefault("42"))
    }

    @Test
    fun nonNegativeIntOrDefault_throws_for_negative() {
        assertFailsWith<FieldValidationError> { Field.nonNegativeIntOrDefault("-1") }
    }

    @Test
    fun nonNegativeDouble_parses_zero() {
        assertEquals(0.0, Field.nonNegativeDouble("0", label = FieldLabel.PricePerUnit))
    }

    @Test
    fun nonNegativeDouble_parses_decimals() {
        assertEquals(2.5, Field.nonNegativeDouble("2.5", label = FieldLabel.PricePerUnit))
    }

    @Test
    fun nonNegativeDouble_throws_for_negative_with_label() {
        val e = assertFailsWith<FieldValidationError> { Field.nonNegativeDouble("-1.5", label = FieldLabel.PricePerUnit) }
        assertIs<FieldValidationError.MustBeNonNegative>(e)
        assertEquals(FieldLabel.PricePerUnit, e.field)
    }

    @Test
    fun nonNegativeDoubleOrDefault_returns_default_for_blank() {
        assertEquals(0.0, Field.nonNegativeDoubleOrDefault("", default = 0.0))
    }

    @Test
    fun nonNegativeDoubleOrDefault_parses_valid() {
        assertEquals(99.99, Field.nonNegativeDoubleOrDefault("99.99"))
    }

    @Test
    fun Check_notBlank_returns_true_for_non_empty() {
        assertTrue(Check.notBlank("a"))
        assertFalse(Check.notBlank(""))
        assertFalse(Check.notBlank("   "))
    }

    @Test
    fun Check_localDate_returns_true_for_iso() {
        assertTrue(Check.localDate("2026-06-07"))
        assertFalse(Check.localDate("07/06/2026"))
        assertFalse(Check.localDate(""))
    }

    @Test
    fun Check_positiveInt_returns_true_for_positive() {
        assertTrue(Check.positiveInt("1"))
        assertFalse(Check.positiveInt("0"))
        assertFalse(Check.positiveInt("-1"))
        assertFalse(Check.positiveInt("abc"))
    }

    @Test
    fun Check_nonNegativeDouble_returns_true_for_zero_and_positive() {
        assertTrue(Check.nonNegativeDouble("0"))
        assertTrue(Check.nonNegativeDouble("1.5"))
        assertFalse(Check.nonNegativeDouble("-0.01"))
        assertFalse(Check.nonNegativeDouble("abc"))
    }
}
