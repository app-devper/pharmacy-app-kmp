@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package app.devper.pharm.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmColumnAlign { Start, Center, End }

data class PharmTableColumn<T>(
    val header: String,
    val weight: Float = 1f,
    val align: PharmColumnAlign = PharmColumnAlign.Start,
    val hideInCompact: Boolean = false,
    val compactTitle: Boolean = false,
    val compactTrailing: Boolean = false,
    val hideInCardWhenEmpty: ((row: T) -> Boolean)? = null,
    val cell: @Composable (row: T) -> Unit,
)

@Composable
fun <T> PharmTable(
    rows: List<T>,
    columns: List<PharmTableColumn<T>>,
    key: ((T) -> Any)? = null,
    modifier: Modifier = Modifier,
    cardModeMaxWidth: Dp = PharmBreakpoint.Medium,
    rowHeight: Dp = Dp.Unspecified,
    headerHeight: Dp = Dp.Unspecified,
    onRowClick: ((T) -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    bottomRow: @Composable (() -> Unit)? = null,
) {
    val t = pharmTokens
    val density = LocalPharmDensity.current
    val effRowHeight = resolvedRowHeight(rowHeight, density)
    val effHeaderHeight = resolvedHeaderHeight(headerHeight, density)
    val listState = rememberLazyListState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.surface),
    ) {
        val tableColumns = remember(columns, maxWidth) { fittedTableColumns(columns, maxWidth) }
        if (rows.isEmpty() && emptyContent != null) {
            if (maxWidth < cardModeMaxWidth) {
                PharmTableCompactEmptySurface(content = emptyContent)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    PharmTableHeader(columns = tableColumns, height = effHeaderHeight)
                    PharmTableEmptySurface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        content = emptyContent,
                    )
                }
            }
            return@BoxWithConstraints
        }

        if (maxWidth < cardModeMaxWidth) {
            PharmTableCardList(
                rows = rows,
                columns = columns,
                key = key,
                onRowClick = onRowClick,
                bottomRow = bottomRow,
            )
            return@BoxWithConstraints
        }

        val minTableWidth = tableMinWidth(tableColumns)
        val needsScroll = minTableWidth > maxWidth
        val hScroll = rememberScrollState()

        val listModifier = if (needsScroll) {
            Modifier.width(minTableWidth).fillMaxHeight()
        } else {
            Modifier.fillMaxWidth().fillMaxHeight()
        }
        val outerModifier = if (needsScroll) {
            Modifier.fillMaxSize().horizontalScroll(hScroll)
        } else {
            Modifier.fillMaxSize()
        }

        Box(modifier = outerModifier) {
            LazyColumn(state = listState, modifier = listModifier) {
                stickyHeader {
                    PharmTableHeader(columns = tableColumns, height = effHeaderHeight)
                }
                items(items = rows, key = key) { row ->
                    val onClickRow = remember(row, onRowClick) {
                        onRowClick?.let { cb -> { cb(row) } }
                    }
                    PharmTableRow(
                        columns = tableColumns,
                        row = row,
                        height = effRowHeight,
                        onClick = onClickRow,
                    )
                }
                if (bottomRow != null) {
                    item { bottomRow() }
                }
            }
        }
    }
}

@Composable
private fun PharmTableCompactEmptySurface(content: @Composable () -> Unit) {
    val t = pharmTokens
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        PharmTableEmptySurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(compactEmptySurfaceHeight(maxHeight)),
            content = content,
        )
    }
}

internal fun compactEmptySurfaceHeight(availableHeight: Dp): Dp =
    (availableHeight * 0.45f)
        .coerceIn(180.dp, 320.dp)
        .coerceAtMost(availableHeight)

@Composable
private fun PharmTableEmptySurface(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    Box(
        modifier = modifier
            .clip(t.shapes.xl)
            .background(t.colors.surfaceRaised)
            .border(1.dp, t.colors.borderSubtle, t.shapes.xl),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val MIN_WIDTH_PER_WEIGHT: Dp = 88.dp

internal fun <T> tableMinWidth(columns: List<PharmTableColumn<T>>): Dp =
    MIN_WIDTH_PER_WEIGHT * columns.fold(0f) { acc, c -> acc + c.weight }

internal fun <T> fittedTableColumns(
    columns: List<PharmTableColumn<T>>,
    availableWidth: Dp,
): List<PharmTableColumn<T>> {
    val kept = columns.toMutableList()
    while (kept.size > 1 && tableMinWidth(kept) > availableWidth) {
        val droppable = kept.indexOfLast { it.hideInCompact }
        if (droppable < 0) break
        kept.removeAt(droppable)
    }
    return kept
}

@Composable
private fun <T> PharmTableCardList(
    rows: List<T>,
    columns: List<PharmTableColumn<T>>,
    key: ((T) -> Any)?,
    onRowClick: ((T) -> Unit)?,
    bottomRow: @Composable (() -> Unit)?,
) {
    val visible = remember(columns) { columns.filterNot { it.hideInCompact } }
    val title = remember(visible) { visible.firstOrNull { it.compactTitle } ?: visible.firstOrNull() }
    val trailing = remember(visible) { visible.firstOrNull { it.compactTrailing } }
    val details = remember(visible, title, trailing) { visible.filter { it !== title && it !== trailing } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(pharmTokens.colors.bgPage),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items(items = rows, key = key) { row ->
            val onClickRow = remember(row, onRowClick) {
                onRowClick?.let { cb -> { cb(row) } }
            }
            PharmTableCard(title = title, details = details, trailing = trailing, row = row, onClick = onClickRow)
        }
        if (bottomRow != null) {
            item { bottomRow() }
        }
    }
}

@Composable
private fun <T> PharmTableCard(
    title: PharmTableColumn<T>?,
    details: List<PharmTableColumn<T>>,
    trailing: PharmTableColumn<T>?,
    row: T,
    onClick: (() -> Unit)?,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = if (onClick != null && hovered) t.colors.hoverSurface else t.colors.bgPage,
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "pharmTableCardBackground",
    )
    val clickable = if (onClick != null) {
        Modifier.pharmClickable(role = Role.Button, interactionSource = interaction, onClick = onClick)
    } else {
        Modifier
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .then(clickable),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                title?.let { Box(modifier = Modifier.fillMaxWidth()) { it.cell(row) } }
                details.forEach { col ->
                    if (col.hideInCardWhenEmpty?.invoke(row) == true) return@forEach
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = col.header,
                            style = PharmText.micro.copy(color = t.colors.fgMuted),
                            modifier = Modifier.weight(1f),
                        )
                        Box(contentAlignment = Alignment.CenterEnd) { col.cell(row) }
                    }
                }
            }
            trailing?.let { Box(contentAlignment = Alignment.TopEnd) { it.cell(row) } }
        }
    }
}

@Composable
private fun <T> PharmTableHeader(columns: List<PharmTableColumn<T>>, height: Dp) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.surfaceRaised),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEach { col ->
                Box(
                    modifier = Modifier.weight(col.weight),
                    contentAlignment = col.align.toBoxAlignment(),
                ) {
                    Text(
                        text = col.header,
                        style = PharmText.thead,
                        textAlign = col.align.toTextAlign(),
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border),
        )
    }
}

@Composable
private fun <T> PharmTableRow(
    columns: List<PharmTableColumn<T>>,
    row: T,
    height: Dp,
    onClick: (() -> Unit)?,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = if (onClick != null && hovered) t.colors.hoverSurface else t.colors.surface,
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "pharmTableRowBackground",
    )
    val clickable = if (onClick != null) {
        Modifier.pharmClickable(role = Role.Button, interactionSource = interaction, onClick = onClick)
    } else {
        Modifier
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = height)
                .background(bg)
                .then(clickable)
                .padding(horizontal = 18.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEach { col ->
                Box(
                    modifier = Modifier.weight(col.weight),
                    contentAlignment = col.align.toBoxAlignment(),
                ) {
                    col.cell(row)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.divider),
        )
    }
}

@Composable
fun PharmTableSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    Box(
        modifier = modifier
            .clip(t.shapes.xl)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.xl),
    ) {
        content()
    }
}

internal fun PharmColumnAlign.toBoxAlignment(): Alignment = when (this) {
    PharmColumnAlign.Start  -> Alignment.CenterStart
    PharmColumnAlign.Center -> Alignment.Center
    PharmColumnAlign.End    -> Alignment.CenterEnd
}

internal fun PharmColumnAlign.toTextAlign(): TextAlign = when (this) {
    PharmColumnAlign.Start  -> TextAlign.Start
    PharmColumnAlign.Center -> TextAlign.Center
    PharmColumnAlign.End    -> TextAlign.End
}
