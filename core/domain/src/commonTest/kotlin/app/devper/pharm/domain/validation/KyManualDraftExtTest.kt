package app.devper.pharm.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KyManualDraftExtTest {

    @Test
    fun common_validators_require_a_valid_date() {
        assertTrue(isKy10DraftValid("2026-06-01", "Drug", "unit", "1"))
        assertTrue(isKy11DraftValid("2026-06-01", "Drug", "unit", "1"))
        assertTrue(isKy12DraftValid("2026-06-01", "Drug", "unit", "1"))

        assertFalse(isKy10DraftValid("2026-99-99", "Drug", "unit", "1"))
        assertFalse(isKy11DraftValid("not-a-date", "Drug", "unit", "1"))
        assertFalse(isKy12DraftValid("", "Drug", "unit", "1"))
    }
}
