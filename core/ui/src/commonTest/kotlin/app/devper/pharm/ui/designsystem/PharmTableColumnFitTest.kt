package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PharmTableColumnFitTest {

    private fun column(header: String, weight: Float, optional: Boolean = false) =
        PharmTableColumn<String>(
            header = header,
            weight = weight,
            hideInCompact = optional,
            cell = {},
        )

    private val stockLikeColumns = listOf(
        column("name", 2.4f),
        column("generic", 1.6f, optional = true),
        column("type", 0.8f, optional = true),
        column("stock", 1.0f),
        column("unit", 0.9f),
        column("actions", 0.6f),
    )

    @Test
    fun everyColumnSurvivesWhenTheTableFits() {
        assertEquals(stockLikeColumns, fittedTableColumns(stockLikeColumns, 1200.dp))
    }

    @Test
    fun onlyAsManyOptionalColumnsAsNeededAreDropped() {
        val fitted = fittedTableColumns(stockLikeColumns, 600.dp)

        assertEquals(listOf("name", "generic", "stock", "unit", "actions"), fitted.map { it.header })
    }

    @Test
    fun narrowerViewportsKeepDroppingFromTheRight() {
        val fitted = fittedTableColumns(stockLikeColumns, 500.dp)

        assertEquals(listOf("name", "stock", "unit", "actions"), fitted.map { it.header })
    }

    @Test
    fun droppingStopsAsSoonAsTheTableFits() {
        listOf(500.dp, 600.dp, 700.dp).forEach { width ->
            assertTrue(tableMinWidth(fittedTableColumns(stockLikeColumns, width)) <= width)
        }
    }

    @Test
    fun essentialColumnsThatStillOverflowAreKeptForHorizontalScroll() {
        val wide = listOf(column("a", 6f), column("b", 6f))

        assertEquals(wide, fittedTableColumns(wide, 300.dp))
        assertEquals(1056.dp, tableMinWidth(wide))
    }

    @Test
    fun aTableOfOnlyOptionalColumnsIsNeverEmptiedCompletely() {
        val allOptional = listOf(
            column("a", 4f, optional = true),
            column("b", 4f, optional = true),
        )

        assertEquals(listOf("a"), fittedTableColumns(allOptional, 400.dp).map { it.header })
        assertEquals(listOf("a"), fittedTableColumns(allOptional, 40.dp).map { it.header })
    }
}
