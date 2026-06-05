@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmColumnAlign { Start, Center, End }

data class PharmTableColumn<T>(
    val header: String,
    val weight: Float = 1f,
    val align: PharmColumnAlign = PharmColumnAlign.Start,
    val hideInCompact: Boolean = false,
    val compactTitle: Boolean = false,
    val hideInCardWhenEmpty: ((row: T) -> Boolean)? = null,
    val cell: @Composable (row: T) -> Unit,
)

@Composable
fun <T> PharmTable(
    rows: List<T>,
    columns: List<PharmTableColumn<T>>,
    key: ((T) -> Any)? = null,
    modifier: Modifier = Modifier,
    rowHeight: Dp = Dp.Unspecified,
    headerHeight: Dp = Dp.Unspecified,
    onRowClick: ((T) -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    bottomRow: @Composable (() -> Unit)? = null,
) {
    val t = pharmTokens
    val density = LocalPharmDensity.current
    val effRowHeight = if (rowHeight == Dp.Unspecified) density.rowHeight else rowHeight
    val effHeaderHeight = if (headerHeight == Dp.Unspecified) density.headerHeight else headerHeight
    val listState = rememberLazyListState()

    if (rows.isEmpty() && emptyContent != null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            emptyContent()
        }
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.surface),
    ) {
        if (maxWidth < CARD_MODE_MAX_WIDTH) {
            PharmTableCardList(
                rows = rows,
                columns = columns,
                key = key,
                onRowClick = onRowClick,
                bottomRow = bottomRow,
            )
            return@BoxWithConstraints
        }

        val totalWeight = remember(columns) { columns.fold(0f) { acc, c -> acc + c.weight } }
        val minTableWidth = MIN_WIDTH_PER_WEIGHT * totalWeight
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
                    PharmTableHeader(columns = columns, height = effHeaderHeight)
                }
                items(items = rows, key = key) { row ->
                    val onClickRow = remember(row, onRowClick) {
                        onRowClick?.let { cb -> { cb(row) } }
                    }
                    PharmTableRow(
                        columns = columns,
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

private val MIN_WIDTH_PER_WEIGHT: Dp = 88.dp
private val CARD_MODE_MAX_WIDTH: Dp = 600.dp

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
    val details = remember(visible, title) { visible.filter { it !== title } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = rows, key = key) { row ->
            val onClickRow = remember(row, onRowClick) {
                onRowClick?.let { cb -> { cb(row) } }
            }
            PharmTableCard(title = title, details = details, row = row, onClick = onClickRow)
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
    row: T,
    onClick: (() -> Unit)?,
) {
    val t = pharmTokens
    val clickable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .then(clickable)
            .padding(horizontal = 14.dp, vertical = 12.dp),
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
}

@Composable
private fun <T> PharmTableHeader(columns: List<PharmTableColumn<T>>, height: Dp) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 12.dp),
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
    val clickable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .then(clickable)
                .padding(horizontal = 12.dp),
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
            .clip(RoundedCornerShape(t.radii.lg))
            .background(t.colors.surface),
    ) {
        content()
    }
}

private fun PharmColumnAlign.toBoxAlignment(): Alignment = when (this) {
    PharmColumnAlign.Start  -> Alignment.CenterStart
    PharmColumnAlign.Center -> Alignment.Center
    PharmColumnAlign.End    -> Alignment.CenterEnd
}

private fun PharmColumnAlign.toTextAlign(): TextAlign = when (this) {
    PharmColumnAlign.Start  -> TextAlign.Start
    PharmColumnAlign.Center -> TextAlign.Center
    PharmColumnAlign.End    -> TextAlign.End
}
