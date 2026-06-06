package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun <T> PharmStaticTable(
    rows: List<T>,
    columns: List<PharmTableColumn<T>>,
    modifier: Modifier = Modifier,
    emptyText: String? = null,
) {
    val t = pharmTokens
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
        RowDivider(t.colors.border)

        if (rows.isEmpty()) {
            if (emptyText != null) {
                Text(
                    text = emptyText,
                    style = PharmText.meta,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            return@Column
        }

        rows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            if (index < rows.lastIndex) RowDivider(t.colors.divider)
        }
    }
}

@Composable
private fun RowDivider(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color),
    )
}
