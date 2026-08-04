package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmSearchFieldTest {
    @Test
    fun passiveSearchFieldShowsDefaultEndIcon() {
        assertTrue(
            showsDefaultEndSearchIcon(
                hasExplicitSearchAction = false,
                hasCustomEndSlot = false,
            ),
        )
    }

    @Test
    fun explicitSearchActionDoesNotDuplicateEndIcon() {
        assertFalse(
            showsDefaultEndSearchIcon(
                hasExplicitSearchAction = true,
                hasCustomEndSlot = false,
            ),
        )
    }

    @Test
    fun customEndIconReplacesDefaultSearchIcon() {
        assertFalse(
            showsDefaultEndSearchIcon(
                hasExplicitSearchAction = false,
                hasCustomEndSlot = true,
            ),
        )
    }

    @Test
    fun searchHeightDoesNotChangeWhenActionOrClearIconAppears() {
        val passiveHeight = singleLineTextFieldHeight(
            minHeight = 36.dp,
            textLineHeight = 20.dp,
            accessoryHeight = 18.dp,
        )
        val actionHeight = singleLineTextFieldHeight(
            minHeight = 36.dp,
            textLineHeight = 20.dp,
            accessoryHeight = 36.dp,
        )

        assertEquals(36.dp, passiveHeight)
        assertEquals(passiveHeight, actionHeight)
    }

    @Test
    fun interactiveSearchActionUsesFieldEndCap() {
        assertEquals(0.dp, textFieldEndPadding(trailingSlotAtEdge = true))
        assertEquals(
            pharmTextFieldHorizontalPadding,
            textFieldEndPadding(trailingSlotAtEdge = false),
        )
    }
}
