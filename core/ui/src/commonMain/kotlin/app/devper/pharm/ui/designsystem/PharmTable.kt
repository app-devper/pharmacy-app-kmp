@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    val cell: @Composable (row: T) -> Unit,
)

@Composable
fun <T> PharmTable(
    rows: List<T>,
    columns: List<PharmTableColumn<T>>,
    key: ((T) -> Any)? = null,
    modifier: Modifier = Modifier,
    rowHeight: Dp = 48.dp,
    headerHeight: Dp = 36.dp,
    onRowClick: ((T) -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    bottomRow: @Composable (() -> Unit)? = null,
) {
    val t = pharmTokens
    val listState = rememberLazyListState()

    if (rows.isEmpty() && emptyContent != null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            emptyContent()
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.surface),
    ) {
        stickyHeader {
            PharmTableHeader(columns = columns, height = headerHeight)
        }
        items(items = rows, key = key) { row ->
            val onClickRow = remember(row, onRowClick) {
                onRowClick?.let { cb -> { cb(row) } }
            }
            PharmTableRow(
                columns = columns,
                row = row,
                height = rowHeight,
                onClick = onClickRow,
            )
        }
        if (bottomRow != null) {
            item { bottomRow() }
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
