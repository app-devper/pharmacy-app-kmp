package app.devper.pharm.presentation.navigation

import app.devper.pharm.ui.i18n.navTitle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.common.platform.UnsavedChangesHandler
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.bulkimport.BulkImport
import app.devper.pharm.presentation.bulkimport.bulkImportNav
import app.devper.pharm.presentation.customers.CustomerAdd
import app.devper.pharm.presentation.customers.CustomerDetail
import app.devper.pharm.presentation.customers.CustomerEdit
import app.devper.pharm.presentation.customers.Customers
import app.devper.pharm.presentation.customers.customersNav
import app.devper.pharm.presentation.expiry.Expiry
import app.devper.pharm.presentation.expiry.expiryNav
import app.devper.pharm.presentation.help.Help
import app.devper.pharm.presentation.help.helpNav
import app.devper.pharm.presentation.imports.ImportDetail
import app.devper.pharm.presentation.imports.ImportEdit
import app.devper.pharm.presentation.imports.ImportNew
import app.devper.pharm.presentation.imports.Imports
import app.devper.pharm.presentation.imports.importsNav
import app.devper.pharm.presentation.ky.Ky9
import app.devper.pharm.presentation.ky.Ky9Add
import app.devper.pharm.presentation.ky.Ky10
import app.devper.pharm.presentation.ky.Ky10Add
import app.devper.pharm.presentation.ky.Ky11
import app.devper.pharm.presentation.ky.Ky11Add
import app.devper.pharm.presentation.ky.Ky12
import app.devper.pharm.presentation.ky.Ky12Add
import app.devper.pharm.presentation.ky.kyNav
import app.devper.pharm.presentation.labels.LabelPrint
import app.devper.pharm.presentation.labels.labelPrintNav
import app.devper.pharm.presentation.movements.Movements
import app.devper.pharm.presentation.movements.movementsNav
import app.devper.pharm.presentation.offlinesync.OfflineSync
import app.devper.pharm.presentation.offlinesync.offlineSyncNav
import app.devper.pharm.presentation.planning.LowStock
import app.devper.pharm.presentation.planning.ReorderSuggestions
import app.devper.pharm.presentation.planning.planningNav
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.profile.profileNav
import app.devper.pharm.presentation.reports.Eod
import app.devper.pharm.presentation.reports.Profit
import app.devper.pharm.presentation.reports.Reports
import app.devper.pharm.presentation.reports.reportsNav
import app.devper.pharm.presentation.saleshistory.SalesHistory
import app.devper.pharm.presentation.saleshistory.salesHistoryNav
import app.devper.pharm.presentation.sell.Cart
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.sell.sellNav
import app.devper.pharm.presentation.settings.Settings as SettingsRoute
import app.devper.pharm.presentation.settings.settingsNav
import app.devper.pharm.presentation.stock.DrugAdd
import app.devper.pharm.presentation.stock.DrugAdjust
import app.devper.pharm.presentation.stock.DrugEdit
import app.devper.pharm.presentation.stock.DrugHistory
import app.devper.pharm.presentation.stock.DrugLots
import app.devper.pharm.presentation.stock.Stock
import app.devper.pharm.presentation.stock.stockNav
import app.devper.pharm.presentation.stockcount.StockCountNew
import app.devper.pharm.presentation.stockcount.StockCounts
import app.devper.pharm.presentation.stockcount.stockCountsNav
import app.devper.pharm.presentation.suppliers.SupplierAdd
import app.devper.pharm.presentation.suppliers.SupplierEdit
import app.devper.pharm.presentation.suppliers.Suppliers
import app.devper.pharm.presentation.suppliers.suppliersNav
import app.devper.pharm.presentation.users.UserAdd
import app.devper.pharm.presentation.users.UserEdit
import app.devper.pharm.presentation.users.Users
import app.devper.pharm.presentation.users.usersNav
import app.devper.pharm.ui.components.AppShell
import app.devper.pharm.ui.components.NavItem
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.TopbarUser
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import kotlin.reflect.KClass

internal fun k(route: KClass<*>): String = requireNotNull(route.qualifiedName)

private data class MainNavEntry(
    val route: Any,
    val label: (PharmStrings) -> String,
    val icon: ImageVector,
    val admin: Boolean = false,
    val pinned: Boolean = false,
    val sectionLabel: (PharmStrings) -> String,
)

private val MAIN_NAV_TABLE: List<MainNavEntry> = listOf(
    MainNavEntry(Sell, { it.navSell }, PharmIcons.Sell, pinned = true, sectionLabel = { it.navGroupSales }),
    MainNavEntry(SalesHistory, { it.navSalesHistory }, PharmIcons.SalesHistory, sectionLabel = { it.navGroupSales }),
    MainNavEntry(Customers, { it.navCustomers }, PharmIcons.Customers, sectionLabel = { it.navGroupSales }),
    MainNavEntry(Stock, { it.navStock }, PharmIcons.Stock, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(StockCounts, { it.navStockCounts }, PharmIcons.StockCount, admin = true, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(Expiry, { it.navExpiry }, PharmIcons.Expiry, admin = true, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(LabelPrint, { it.navLabelPrint }, PharmIcons.Print, admin = true, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(Movements, { it.navMovements }, PharmIcons.Movements, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(OfflineSync, { it.navOfflineSync }, PharmIcons.OfflineSync, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(Imports, { it.navImports }, PharmIcons.Imports, admin = true, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(Suppliers, { it.navSuppliers }, PharmIcons.Suppliers, admin = true, sectionLabel = { it.navGroupInventory }),
    MainNavEntry(Reports, { it.navReports }, PharmIcons.Reports, sectionLabel = { it.navGroupReports }),
    MainNavEntry(Profit, { it.navProfit }, PharmIcons.Profit, admin = true, sectionLabel = { it.navGroupReports }),
    MainNavEntry(Ky9, { it.navKyForms }, PharmIcons.KyForms, admin = true, sectionLabel = { it.navGroupReports }),
    MainNavEntry(Users, { it.navUsers }, PharmIcons.Users, admin = true, sectionLabel = { it.navGroupSystem }),
    MainNavEntry(SettingsRoute, { it.navSettings }, PharmIcons.Settings, admin = true, sectionLabel = { it.navGroupSystem }),
    MainNavEntry(Help, { it.navHelp }, PharmIcons.Help, sectionLabel = { it.navGroupSystem }),
)

@Composable
internal fun rememberMainNavItems(): List<NavItem> {
    val strings = pharmStrings
    return MAIN_NAV_TABLE.map { entry ->
        NavItem(
            route = k(entry.route::class),
            label = entry.label(strings),
            icon = entry.icon,
            admin = entry.admin,
            pinned = entry.pinned,
            sectionLabel = entry.sectionLabel(strings),
        )
    }
}

internal fun routeForKey(key: String): Any? =
    MAIN_NAV_TABLE.firstOrNull { k(it.route::class) == key }?.route

private data class DestInfo(val title: (PharmStrings) -> String, val sectionKey: String?)

private val DEST_INFO: Map<String, DestInfo> = buildMap {
    fun add(route: KClass<*>, title: (PharmStrings) -> String, section: KClass<*>?) {
        put(k(route), DestInfo(title, section?.let(::k)))
    }
    add(Sell::class, { it.titleSell }, Sell::class)
    add(Cart::class, { it.titleSell }, Sell::class)
    add(SalesHistory::class, { it.navSalesHistory }, SalesHistory::class)
    add(Stock::class, { it.navStock }, Stock::class)
    add(DrugAdd::class, { it.navStock }, Stock::class)
    add(DrugEdit::class, { it.navStock }, Stock::class)
    add(DrugLots::class, { it.navStock }, Stock::class)
    add(DrugAdjust::class, { it.navStock }, Stock::class)
    add(DrugHistory::class, { it.navStock }, Stock::class)
    add(ReorderSuggestions::class, { it.navStock }, Stock::class)
    add(StockCounts::class, { it.navStockCounts }, StockCounts::class)
    add(StockCountNew::class, { it.navStockCounts }, StockCounts::class)
    add(Expiry::class, { it.navExpiry }, Expiry::class)
    add(LabelPrint::class, { it.navLabelPrint }, LabelPrint::class)
    add(Movements::class, { it.navMovements }, Movements::class)
    add(OfflineSync::class, { it.navOfflineSync }, OfflineSync::class)
    add(Imports::class, { it.navImports }, Imports::class)
    add(ImportNew::class, { it.navImports }, Imports::class)
    add(ImportEdit::class, { it.navImports }, Imports::class)
    add(ImportDetail::class, { it.navImports }, Imports::class)
    add(Suppliers::class, { it.navSuppliers }, Suppliers::class)
    add(SupplierAdd::class, { it.navSuppliers }, Suppliers::class)
    add(SupplierEdit::class, { it.navSuppliers }, Suppliers::class)
    add(Customers::class, { it.navCustomers }, Customers::class)
    add(CustomerAdd::class, { it.navCustomers }, Customers::class)
    add(CustomerEdit::class, { it.navCustomers }, Customers::class)
    add(CustomerDetail::class, { it.navCustomers }, Customers::class)
    add(Reports::class, { it.navReports }, Reports::class)
    add(Eod::class, { it.navReports }, Reports::class)
    add(Profit::class, { it.navProfit }, Profit::class)
    add(Ky9::class, { KyFormType.Ky9.navTitle(it) }, Ky9::class)
    add(Ky9Add::class, { KyFormType.Ky9.navTitle(it) }, Ky9::class)
    add(Ky10::class, { KyFormType.Ky10.navTitle(it) }, Ky9::class)
    add(Ky10Add::class, { KyFormType.Ky10.navTitle(it) }, Ky9::class)
    add(Ky11::class, { KyFormType.Ky11.navTitle(it) }, Ky9::class)
    add(Ky11Add::class, { KyFormType.Ky11.navTitle(it) }, Ky9::class)
    add(Ky12::class, { KyFormType.Ky12.navTitle(it) }, Ky9::class)
    add(Ky12Add::class, { KyFormType.Ky12.navTitle(it) }, Ky9::class)
    add(Users::class, { it.navUsers }, Users::class)
    add(UserAdd::class, { it.navUsers }, Users::class)
    add(UserEdit::class, { it.navUsers }, Users::class)
    add(SettingsRoute::class, { it.navSettings }, SettingsRoute::class)
    add(Help::class, { it.navHelp }, Help::class)
    add(BulkImport::class, { it.navBulkImport }, null)
    add(LowStock::class, { it.navLowStock }, null)
    add(Profile::class, { it.profileSectionPersonal }, null)
}

internal val SUB_PAGE_ROUTE_KEYS: Set<String> = setOf(
    k(Cart::class), k(CustomerAdd::class), k(CustomerEdit::class), k(CustomerDetail::class),
    k(ImportNew::class), k(ImportEdit::class), k(ImportDetail::class),
    k(Ky9Add::class), k(Ky10Add::class), k(Ky11Add::class), k(Ky12Add::class),
    k(ReorderSuggestions::class), k(Eod::class),
    k(DrugAdd::class), k(DrugEdit::class), k(DrugLots::class), k(DrugAdjust::class), k(DrugHistory::class),
    k(StockCountNew::class), k(SupplierAdd::class), k(SupplierEdit::class),
    k(UserAdd::class), k(UserEdit::class),
    k(Profile::class),
)

@Composable
internal fun destInfoFor(route: String?): Pair<String, String?> {
    val strings = pharmStrings
    val base = route?.substringBefore('/')?.substringBefore('?')
    val info = base?.let { DEST_INFO[it] }
    val title = info?.title?.invoke(strings).orEmpty()
    return title to info?.sectionKey
}
