package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.WindowSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmActionMenuTest {
    @Test
    fun compactWindowUsesBottomSheet() {
        assertTrue(usesActionBottomSheet(WindowSize.Compact))
        assertFalse(usesActionBottomSheet(WindowSize.Medium))
        assertFalse(usesActionBottomSheet(WindowSize.Expanded))
    }

    @Test
    fun bottomSheetStopsAtMediumBreakpoint() {
        assertTrue(usesActionBottomSheet(WindowSize.fromWidth(599.dp)))
        assertFalse(usesActionBottomSheet(WindowSize.fromWidth(600.dp)))
    }

    @Test
    fun downSelectsFirstEnabledActionWhenNothingIsFocused() {
        assertEquals(
            expected = 1,
            actual = nextEnabledActionIndex(
                enabled = listOf(false, true, true),
                currentIndex = -1,
                direction = 1,
            ),
        )
    }

    @Test
    fun navigationSkipsDisabledActionsAndWraps() {
        val enabled = listOf(true, false, true)

        assertEquals(2, nextEnabledActionIndex(enabled, currentIndex = 0, direction = 1))
        assertEquals(0, nextEnabledActionIndex(enabled, currentIndex = 2, direction = 1))
        assertEquals(2, nextEnabledActionIndex(enabled, currentIndex = 0, direction = -1))
    }

    @Test
    fun upSelectsLastEnabledActionWhenNothingIsFocused() {
        assertEquals(
            expected = 1,
            actual = nextEnabledActionIndex(
                enabled = listOf(true, true, false),
                currentIndex = -1,
                direction = -1,
            ),
        )
    }

    @Test
    fun navigationReturnsNoSelectionWhenEveryActionIsDisabled() {
        assertEquals(
            expected = -1,
            actual = nextEnabledActionIndex(
                enabled = listOf(false, false),
                currentIndex = -1,
                direction = 1,
            ),
        )
    }

    @Test
    fun firstFocusSkipsDisabledActions() {
        assertEquals(
            expected = 2,
            actual = actionFocusTargetIndex(
                enabled = listOf(false, false, true, true),
                currentIndex = -1,
                move = PharmActionFocusMove.First,
            ),
        )
    }

    @Test
    fun homeAndEndSelectBoundaryEnabledActions() {
        val enabled = listOf(false, true, false, true, false)

        assertEquals(
            expected = 1,
            actual = actionFocusTargetIndex(
                enabled = enabled,
                currentIndex = 3,
                move = PharmActionFocusMove.First,
            ),
        )
        assertEquals(
            expected = 3,
            actual = actionFocusTargetIndex(
                enabled = enabled,
                currentIndex = 1,
                move = PharmActionFocusMove.Last,
            ),
        )
    }

    @Test
    fun everyFocusMoveReturnsNoSelectionWhenActionsAreDisabled() {
        PharmActionFocusMove.entries.forEach { move ->
            assertEquals(
                expected = -1,
                actual = actionFocusTargetIndex(
                    enabled = listOf(false, false),
                    currentIndex = -1,
                    move = move,
                ),
            )
        }
    }
}
