package app.devper.pharm.ui.components

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.formContentHorizontalPadding
import kotlin.test.Test
import kotlin.test.assertEquals

class ResponsiveBreakpointsTest {

    @Test
    fun targetQaWidthsResolveToExpectedLayouts() {
        assertEquals(WindowSize.Compact, WindowSize.fromWidth(320.dp))
        assertEquals(WindowSize.Compact, WindowSize.fromWidth(360.dp))
        assertEquals(WindowSize.Medium, WindowSize.fromWidth(600.dp))
        assertEquals(WindowSize.Expanded, WindowSize.fromWidth(840.dp))
    }

    @Test
    fun formsUseMoreContentWidthOnPhones() {
        assertEquals(16.dp, formContentHorizontalPadding(WindowSize.fromWidth(320.dp)))
        assertEquals(16.dp, formContentHorizontalPadding(WindowSize.fromWidth(360.dp)))
        assertEquals(24.dp, formContentHorizontalPadding(WindowSize.fromWidth(600.dp)))
        assertEquals(24.dp, formContentHorizontalPadding(WindowSize.fromWidth(840.dp)))
    }
}
