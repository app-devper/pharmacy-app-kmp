package app.devper.pharm.presentation.navigation

import app.devper.pharm.ui.i18n.navTitle
import androidx.compose.runtime.Composable
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.model.Role
import app.devper.pharm.presentation.bulkimport.BulkImport
import app.devper.pharm.presentation.customers.CustomerAdd
import app.devper.pharm.presentation.customers.CustomerDetail
import app.devper.pharm.presentation.customers.CustomerEdit
import app.devper.pharm.presentation.customers.Customers
import app.devper.pharm.presentation.expiry.Expiry
import app.devper.pharm.presentation.help.Help
import app.devper.pharm.presentation.imports.ImportDetail
import app.devper.pharm.presentation.imports.ImportEdit
import app.devper.pharm.presentation.imports.ImportNew
import app.devper.pharm.presentation.imports.Imports
import app.devper.pharm.presentation.ky.Ky9
import app.devper.pharm.presentation.ky.Ky9Add
import app.devper.pharm.presentation.ky.Ky10
import app.devper.pharm.presentation.ky.Ky10Add
import app.devper.pharm.presentation.ky.Ky11
import app.devper.pharm.presentation.ky.Ky11Add
import app.devper.pharm.presentation.ky.Ky12
import app.devper.pharm.presentation.ky.Ky12Add
import app.devper.pharm.presentation.labels.LabelPrint
import app.devper.pharm.presentation.movements.Movements
import app.devper.pharm.presentation.offlinesync.OfflineSync
import app.devper.pharm.presentation.planning.LowStock
import app.devper.pharm.presentation.planning.ReorderSuggestions
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.reports.Eod
import app.devper.pharm.presentation.reports.Profit
import app.devper.pharm.presentation.reports.Reports
import app.devper.pharm.presentation.saleshistory.SalesHistory
import app.devper.pharm.presentation.sell.Cart
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.settings.Settings as SettingsRoute
import app.devper.pharm.presentation.stock.DrugAdd
import app.devper.pharm.presentation.stock.DrugAdjust
import app.devper.pharm.presentation.stock.DrugEdit
import app.devper.pharm.presentation.stock.DrugHistory
import app.devper.pharm.presentation.stock.DrugLots
import app.devper.pharm.presentation.stock.Stock
import app.devper.pharm.presentation.stockcount.StockCountNew
import app.devper.pharm.presentation.stockcount.StockCounts
import app.devper.pharm.presentation.suppliers.SupplierAdd
import app.devper.pharm.presentation.suppliers.SupplierEdit
import app.devper.pharm.presentation.suppliers.Suppliers
import app.devper.pharm.presentation.users.UserAdd
import app.devper.pharm.presentation.users.UserEdit
import app.devper.pharm.presentation.users.Users
import app.devper.pharm.ui.components.NavItem
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings
import kotlin.reflect.KClass

internal fun k(route: KClass<*>): String = requireNotNull(route.qualifiedName)

internal val mainDestinations: List<MainDestination> = listOf(
    MainDestination(
        Sell::class, { it.titleSell }, Sell::class,
        sidebar = MainSidebarEntry(
            target = Sell,
            icon = PharmIcons.Sell,
            label = { it.navSell },
            pinned = true,
            sectionLabel = { it.navGroupSales },
        ),
    ),
    MainDestination(
        SalesHistory::class, { it.navSalesHistory }, SalesHistory::class,
        sidebar = MainSidebarEntry(
            target = SalesHistory,
            icon = PharmIcons.SalesHistory,
            sectionLabel = { it.navGroupSales },
        ),
    ),
    MainDestination(
        Customers::class, { it.navCustomers }, Customers::class,
        sidebar = MainSidebarEntry(
            target = Customers,
            icon = PharmIcons.Customers,
            sectionLabel = { it.navGroupSales },
        ),
    ),
    MainDestination(
        Stock::class, { it.navStock }, Stock::class,
        sidebar = MainSidebarEntry(
            target = Stock,
            icon = PharmIcons.Stock,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        StockCounts::class, { it.navStockCounts }, StockCounts::class,
        sidebar = MainSidebarEntry(
            target = StockCounts,
            icon = PharmIcons.StockCount,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        Expiry::class, { it.navExpiry }, Expiry::class,
        sidebar = MainSidebarEntry(
            target = Expiry,
            icon = PharmIcons.Expiry,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        LabelPrint::class, { it.navLabelPrint }, LabelPrint::class,
        sidebar = MainSidebarEntry(
            target = LabelPrint,
            icon = PharmIcons.Print,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        Movements::class, { it.navMovements }, Movements::class,
        sidebar = MainSidebarEntry(
            target = Movements,
            icon = PharmIcons.Movements,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        OfflineSync::class, { it.navOfflineSync }, OfflineSync::class,
        sidebar = MainSidebarEntry(
            target = OfflineSync,
            icon = PharmIcons.OfflineSync,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        Imports::class, { it.navImports }, Imports::class,
        sidebar = MainSidebarEntry(
            target = Imports,
            icon = PharmIcons.Imports,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        Suppliers::class, { it.navSuppliers }, Suppliers::class,
        sidebar = MainSidebarEntry(
            target = Suppliers,
            icon = PharmIcons.Suppliers,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupInventory },
        ),
    ),
    MainDestination(
        Reports::class, { it.navReports }, Reports::class,
        sidebar = MainSidebarEntry(
            target = Reports,
            icon = PharmIcons.Reports,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupReports },
        ),
    ),
    MainDestination(
        Profit::class, { it.navProfit }, Profit::class,
        sidebar = MainSidebarEntry(
            target = Profit,
            icon = PharmIcons.Profit,
            minRole = Role.ADMIN,
            sectionLabel = { it.navGroupReports },
        ),
    ),
    MainDestination(
        Ky9::class, { KyFormType.Ky9.navTitle(it) }, Ky9::class,
        sidebar = MainSidebarEntry(
            target = Ky9,
            icon = PharmIcons.KyForms,
            label = { it.navKyForms },
            minRole = Role.ADMIN,
            sectionLabel = { it.navGroupReports },
        ),
    ),
    MainDestination(
        Users::class, { it.navUsers }, Users::class,
        sidebar = MainSidebarEntry(
            target = Users,
            icon = PharmIcons.Users,
            minRole = Role.MANAGER,
            sectionLabel = { it.navGroupSystem },
        ),
    ),
    MainDestination(
        SettingsRoute::class, { it.navSettings }, SettingsRoute::class,
        sidebar = MainSidebarEntry(
            target = SettingsRoute,
            icon = PharmIcons.Settings,
            minRole = Role.ADMIN,
            sectionLabel = { it.navGroupSystem },
        ),
    ),
    MainDestination(
        Help::class, { it.navHelp }, Help::class,
        sidebar = MainSidebarEntry(
            target = Help,
            icon = PharmIcons.Help,
            sectionLabel = { it.navGroupSystem },
        ),
    ),
    MainDestination(Cart::class, { it.titleSell }, Sell::class, isSubPage = true),
    MainDestination(DrugAdd::class, { it.navStock }, Stock::class, isSubPage = true),
    MainDestination(DrugEdit::class, { it.navStock }, Stock::class, isSubPage = true),
    MainDestination(DrugLots::class, { it.navStock }, Stock::class, isSubPage = true),
    MainDestination(DrugAdjust::class, { it.navStock }, Stock::class, isSubPage = true),
    MainDestination(DrugHistory::class, { it.navStock }, Stock::class, isSubPage = true),
    MainDestination(ReorderSuggestions::class, { it.navStock }, Stock::class, isSubPage = true),
    MainDestination(StockCountNew::class, { it.navStockCounts }, StockCounts::class, isSubPage = true),
    MainDestination(ImportNew::class, { it.navImports }, Imports::class, isSubPage = true),
    MainDestination(ImportEdit::class, { it.navImports }, Imports::class, isSubPage = true),
    MainDestination(ImportDetail::class, { it.navImports }, Imports::class, isSubPage = true),
    MainDestination(SupplierAdd::class, { it.navSuppliers }, Suppliers::class, isSubPage = true),
    MainDestination(SupplierEdit::class, { it.navSuppliers }, Suppliers::class, isSubPage = true),
    MainDestination(CustomerAdd::class, { it.navCustomers }, Customers::class, isSubPage = true),
    MainDestination(CustomerEdit::class, { it.navCustomers }, Customers::class, isSubPage = true),
    MainDestination(CustomerDetail::class, { it.navCustomers }, Customers::class, isSubPage = true),
    MainDestination(Eod::class, { it.navReports }, Reports::class, isSubPage = true),
    MainDestination(Ky9Add::class, { KyFormType.Ky9.navTitle(it) }, Ky9::class, isSubPage = true),
    MainDestination(Ky10::class, { KyFormType.Ky10.navTitle(it) }, Ky9::class),
    MainDestination(Ky10Add::class, { KyFormType.Ky10.navTitle(it) }, Ky9::class, isSubPage = true),
    MainDestination(Ky11::class, { KyFormType.Ky11.navTitle(it) }, Ky9::class),
    MainDestination(Ky11Add::class, { KyFormType.Ky11.navTitle(it) }, Ky9::class, isSubPage = true),
    MainDestination(Ky12::class, { KyFormType.Ky12.navTitle(it) }, Ky9::class),
    MainDestination(Ky12Add::class, { KyFormType.Ky12.navTitle(it) }, Ky9::class, isSubPage = true),
    MainDestination(UserAdd::class, { it.navUsers }, Users::class, isSubPage = true),
    MainDestination(UserEdit::class, { it.navUsers }, Users::class, isSubPage = true),
    MainDestination(BulkImport::class, { it.navBulkImport }, null),
    MainDestination(LowStock::class, { it.navLowStock }, null),
    MainDestination(Profile::class, { it.profileSectionPersonal }, null, isSubPage = true),
)

private val destinationsByKey = mainDestinations.associateBy { k(it.route) }

internal fun destinationFor(route: String?): MainDestination? = destinationsByKey[route.toRouteKey()]

internal fun mainNavItems(strings: PharmStrings): List<NavItem> = mainDestinations.mapNotNull { destination ->
    destination.sidebar?.let { entry ->
        NavItem(
            route = k(destination.route),
            label = entry.label?.invoke(strings) ?: destination.title(strings),
            icon = entry.icon,
            minRole = entry.minRole,
            pinned = entry.pinned,
            sectionLabel = entry.sectionLabel(strings),
        )
    }
}

@Composable
internal fun rememberMainNavItems(): List<NavItem> = mainNavItems(pharmStrings)

internal fun routeForKey(key: String): Any? = destinationsByKey[key]?.sidebar?.target

internal fun String?.toRouteKey(): String? = this?.substringBefore('/')?.substringBefore('?')
