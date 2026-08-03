package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.LocalWindowSize
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
    val compact = LocalWindowSize.current.isCompact
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = t.dimens.listWorkspaceMaxWidth)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
        ) {
            if (toolbar != null) {
                toolbar()
            }
            if (metrics != null || banner != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    metrics?.invoke()
                    banner?.invoke()
                }
            }
            resultLine()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(
                        if (compact) {
                            Modifier.background(t.colors.bgPage)
                        } else {
                            Modifier
                                .padding(horizontal = 16.dp)
                                .clip(t.shapes.xl)
                                .background(t.colors.surface)
                                .border(1.dp, t.colors.borderSubtle, t.shapes.xl)
                        },
                    ),
                content = content,
            )
        }
        footer?.let {
            Box(
                modifier = Modifier
                    .widthIn(max = t.dimens.listWorkspaceMaxWidth)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            ) { it() }
        }
    }
}
