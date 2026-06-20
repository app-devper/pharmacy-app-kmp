package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmListScaffold(
    resultLine: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    metrics: (@Composable () -> Unit)? = null,
    banner: (@Composable () -> Unit)? = null,
    toolbar: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        metrics?.let {
            Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp)) { it() }
        }
        banner?.let {
            Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp)) { it() }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            if (toolbar != null) {
                toolbar()
                PharmListHairline()
            }
            resultLine()
            PharmListHairline()
            content()
        }
        footer?.let {
            Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) { it() }
        }
    }
}

@Composable
private fun PharmListHairline() {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
}
