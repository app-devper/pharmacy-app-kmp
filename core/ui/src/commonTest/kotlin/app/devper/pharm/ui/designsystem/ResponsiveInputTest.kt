package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResponsiveInputTest {
    @Test
    fun dateRangeStacksBelowStackBreakpoint() {
        assertTrue(shouldStackDateRange(320.dp))
        assertTrue(shouldStackDateRange(359.dp))
        assertFalse(shouldStackDateRange(360.dp))
    }

    @Test
    fun datePickerFooterStacksOnlyWhenActionsAreConstrained() {
        assertTrue(shouldStackDatePickerFooter(280.dp))
        assertTrue(shouldStackDatePickerFooter(299.dp))
        assertFalse(shouldStackDatePickerFooter(300.dp))
    }
}
