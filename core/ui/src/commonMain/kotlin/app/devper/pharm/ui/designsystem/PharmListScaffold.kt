package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.theme.pharmTokens
import kotlin.math.roundToInt

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
    val headerState = rememberCollapsibleHeaderState()
    val collapsesMetrics = compact && metrics != null
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
            if (collapsesMetrics) {
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clipToBounds()
                        .nestedScroll(headerState.nestedScrollConnection()),
                ) {
                    val headerHeight = with(LocalDensity.current) { headerState.headerHeightPx.toDp() }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maxHeight + headerHeight)
                            .offset { IntOffset(0, headerState.offsetPx.roundToInt()) },
                    ) {
                        MetricsBanner(
                            metrics = metrics,
                            banner = banner,
                            modifier = Modifier.onSizeChanged { headerState.onHeaderMeasured(it.height) },
                        )
                        resultLine()
                        ListBody(compact = compact, content = content)
                    }
                }
            } else {
                if (metrics != null || banner != null) {
                    MetricsBanner(metrics = metrics, banner = banner)
                }
                resultLine()
                ListBody(compact = compact, content = content)
            }
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

@Composable
private fun MetricsBanner(
    metrics: (@Composable () -> Unit)?,
    banner: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        metrics?.invoke()
        banner?.invoke()
    }
}

@Composable
private fun ColumnScope.ListBody(
    compact: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    val t = pharmTokens
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
