package app.devper.pharm.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.lifecycle.ViewModelStore
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.stock.DrugEdit
import app.devper.pharm.presentation.stock.Stock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import javax.swing.SwingUtilities

class MainSectionNavigationTest {
    @Test
    fun switching_from_edit_page_and_returning_preserves_list_entry_without_restoring_edit_page() = SwingUtilities.invokeAndWait {
        val nav = NavHostController()
        nav.setViewModelStore(ViewModelStore())
        nav.navigatorProvider.addNavigator(ComposeNavigator())
        nav.graph = nav.createGraph(startDestination = Sell) {
            composable<Sell> {}
            composable<Stock> {}
            composable<DrugEdit> {}
        }

        nav.navigate(Stock)
        val stockEntry = nav.getBackStackEntry<Stock>()
        stockEntry.savedStateHandle["listQuery"] = "aspirin"
        nav.navigate(DrugEdit("drug-1"))

        nav.navigateToSection(Sell)
        assertEquals(k(Sell::class), nav.currentDestination?.route)
        assertFailsWith<IllegalArgumentException> { nav.getBackStackEntry<DrugEdit>() }

        nav.navigateToSection(Stock)
        assertEquals(k(Stock::class), nav.currentDestination?.route)
        assertEquals(stockEntry.id, nav.getBackStackEntry<Stock>().id)
        assertEquals("aspirin", nav.getBackStackEntry<Stock>().savedStateHandle["listQuery"])
    }
}
