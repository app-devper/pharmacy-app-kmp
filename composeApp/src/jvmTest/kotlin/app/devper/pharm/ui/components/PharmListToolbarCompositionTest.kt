package app.devper.pharm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.theme.PharmacyTheme
import org.junit.Rule
import org.junit.Test

class PharmListToolbarCompositionTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun actions_render_once_across_width_and_search_then_disappear_when_page_leaves() {
        var width by mutableIntStateOf(320)
        var searching by mutableStateOf(false)
        var pageVisible by mutableStateOf(true)
        val topbarActions = CompactPageChromeController()

        composeRule.setContent {
            PharmacyTheme(darkTheme = false) {
                CompositionLocalProvider(
                    LocalWindowSize provides WindowSize.fromWidth(width.dp),
                    LocalCompactPageChromeController provides topbarActions,
                ) {
                    Column {
                        if (pageVisible) {
                            Box(Modifier.width(width.dp)) {
                                PharmListToolbar(
                                    title = "Products",
                                    searchValue = if (searching) "aspirin" else null,
                                    onSearchChange = {},
                                    actions = { Text("Secondary action") },
                                    primaryAction = { Text("Primary action") },
                                )
                            }
                        }
                        topbarActions.content?.actions?.invoke()
                    }
                }
            }
        }

        listOf(320, 600, 840).forEach { nextWidth ->
            composeRule.runOnIdle { width = nextWidth }
            composeRule.onAllNodesWithText("Primary action").assertCountEquals(1)
            composeRule.onAllNodesWithText("Secondary action").assertCountEquals(1)
            composeRule.runOnIdle { searching = true }
            composeRule.onAllNodesWithText("Primary action").assertCountEquals(1)
            composeRule.onAllNodesWithText("Secondary action").assertCountEquals(1)
        }

        composeRule.runOnIdle { pageVisible = false }
        composeRule.onAllNodesWithText("Primary action").assertCountEquals(0)
        composeRule.onAllNodesWithText("Secondary action").assertCountEquals(0)
    }
}
