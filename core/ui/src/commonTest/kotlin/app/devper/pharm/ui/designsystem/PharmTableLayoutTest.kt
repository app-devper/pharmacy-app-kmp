package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class PharmTableLayoutTest {
    @Test
    fun compactEmptySurfaceShrinksOnShortViewports() {
        assertEquals(180.dp, compactEmptySurfaceHeight(300.dp))
        assertEquals(180.dp, compactEmptySurfaceHeight(400.dp))
    }

    @Test
    fun compactEmptySurfaceCapsHeightOnTallViewports() {
        assertEquals(320.dp, compactEmptySurfaceHeight(900.dp))
    }
}
