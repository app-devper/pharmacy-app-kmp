package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class PharmButtonLayoutTest {

    @Test
    fun compactContentButtonsKeepMinimumTouchHeight() {
        assertEquals(
            36.dp,
            responsiveButtonMinHeight(
                baseMinHeight = 32.dp,
                compact = true,
                compactTopbarAction = false,
                minimumTouchTarget = 36.dp,
            ),
        )
        assertEquals(
            36.dp,
            responsiveButtonMinHeight(
                baseMinHeight = 32.dp,
                compact = true,
                compactTopbarAction = true,
                minimumTouchTarget = 36.dp,
            ),
        )
        assertEquals(
            36.dp,
            responsiveButtonMinHeight(
                baseMinHeight = 32.dp,
                compact = false,
                compactTopbarAction = true,
                minimumTouchTarget = 36.dp,
            ),
        )
        assertEquals(
            32.dp,
            responsiveButtonMinHeight(
                baseMinHeight = 32.dp,
                compact = false,
                compactTopbarAction = false,
                minimumTouchTarget = 36.dp,
            ),
        )
    }
}
