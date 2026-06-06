package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.devper.pharm.domain.model.KyFormType
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
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data object MainRoot

private fun k(route: KClass<*>): String = requireNotNull(route.qualifiedName)

private data class MainNavEntry(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val admin: Boolean = false,
)

private val MAIN_NAV_TABLE: List<MainNavEntry> = listOf(
    MainNavEntry(Sell, "หน้าขายยา", PharmIcons.Sell),
    MainNavEntry(SalesHistory, "ประวัติการขาย", PharmIcons.SalesHistory),
    MainNavEntry(Stock, "สต็อกยา", PharmIcons.Stock),
    MainNavEntry(StockCounts, "ตรวจนับสต็อก", PharmIcons.StockCount, admin = true),
    MainNavEntry(Expiry, "จัดการวันหมดอายุ", PharmIcons.Expiry, admin = true),
    MainNavEntry(LabelPrint, "พิมพ์ฉลาก", PharmIcons.Print, admin = true),
    MainNavEntry(Movements, "ความเคลื่อนไหวสต็อก", PharmIcons.Movements),
    MainNavEntry(OfflineSync, "รายการค้างซิงค์", PharmIcons.OfflineSync),
    MainNavEntry(Imports, "นำเข้าสินค้า", PharmIcons.Imports, admin = true),
    MainNavEntry(Suppliers, "ซัพพลายเออร์", PharmIcons.Suppliers, admin = true),
    MainNavEntry(Customers, "ลูกค้า", PharmIcons.Customers),
    MainNavEntry(Reports, "รายงาน", PharmIcons.Reports),
    MainNavEntry(Profit, "กำไร", PharmIcons.Profit, admin = true),
    MainNavEntry(Ky9, "แบบฟอร์ม ขย. 9–12", PharmIcons.KyForms, admin = true),
    MainNavEntry(Users, "จัดการผู้ใช้งาน", PharmIcons.Users, admin = true),
    MainNavEntry(SettingsRoute, "ตั้งค่าระบบ", PharmIcons.Settings, admin = true),
    MainNavEntry(Help, "คู่มือการใช้งาน", PharmIcons.Help),
)

private val mainNavItems: List<NavItem> = MAIN_NAV_TABLE.map { entry ->
    NavItem(route = k(entry.route::class), label = entry.label, icon = entry.icon, admin = entry.admin)
}

private fun routeForKey(key: String): Any? =
    MAIN_NAV_TABLE.firstOrNull { k(it.route::class) == key }?.route

private data class DestInfo(val title: String, val sectionKey: String?)

private val DEST_INFO: Map<String, DestInfo> = buildMap {
    fun add(route: KClass<*>, title: String, section: KClass<*>?) {
        put(k(route), DestInfo(title, section?.let(::k)))
    }
    add(Sell::class, "ขายยา", Sell::class)
    add(Cart::class, "ขายยา", Sell::class)
    add(SalesHistory::class, "ประวัติการขาย", SalesHistory::class)
    add(Stock::class, "สต็อกยา", Stock::class)
    add(DrugAdd::class, "สต็อกยา", Stock::class)
    add(DrugEdit::class, "สต็อกยา", Stock::class)
    add(DrugLots::class, "สต็อกยา", Stock::class)
    add(DrugAdjust::class, "สต็อกยา", Stock::class)
    add(DrugHistory::class, "สต็อกยา", Stock::class)
    add(ReorderSuggestions::class, "สต็อกยา", Stock::class)
    add(StockCounts::class, "ตรวจนับสต็อก", StockCounts::class)
    add(StockCountNew::class, "ตรวจนับสต็อก", StockCounts::class)
    add(Expiry::class, "จัดการวันหมดอายุ", Expiry::class)
    add(LabelPrint::class, "พิมพ์ฉลาก", LabelPrint::class)
    add(Movements::class, "ความเคลื่อนไหวสต็อก", Movements::class)
    add(OfflineSync::class, "รายการค้างซิงค์", OfflineSync::class)
    add(Imports::class, "นำเข้าสินค้า", Imports::class)
    add(ImportNew::class, "นำเข้าสินค้า", Imports::class)
    add(ImportEdit::class, "นำเข้าสินค้า", Imports::class)
    add(ImportDetail::class, "นำเข้าสินค้า", Imports::class)
    add(Suppliers::class, "ซัพพลายเออร์", Suppliers::class)
    add(SupplierAdd::class, "ซัพพลายเออร์", Suppliers::class)
    add(SupplierEdit::class, "ซัพพลายเออร์", Suppliers::class)
    add(Customers::class, "ลูกค้า", Customers::class)
    add(CustomerAdd::class, "ลูกค้า", Customers::class)
    add(CustomerEdit::class, "ลูกค้า", Customers::class)
    add(CustomerDetail::class, "ลูกค้า", Customers::class)
    add(Reports::class, "รายงาน", Reports::class)
    add(Eod::class, "รายงาน", Reports::class)
    add(Profit::class, "กำไร", Profit::class)
    add(Ky9::class, KyFormType.Ky9.label, Ky9::class)
    add(Ky9Add::class, KyFormType.Ky9.label, Ky9::class)
    add(Ky10::class, KyFormType.Ky10.label, Ky9::class)
    add(Ky10Add::class, KyFormType.Ky10.label, Ky9::class)
    add(Ky11::class, KyFormType.Ky11.label, Ky9::class)
    add(Ky11Add::class, KyFormType.Ky11.label, Ky9::class)
    add(Ky12::class, KyFormType.Ky12.label, Ky9::class)
    add(Ky12Add::class, KyFormType.Ky12.label, Ky9::class)
    add(Users::class, "จัดการผู้ใช้งาน", Users::class)
    add(UserAdd::class, "จัดการผู้ใช้งาน", Users::class)
    add(UserEdit::class, "จัดการผู้ใช้งาน", Users::class)
    add(SettingsRoute::class, "ตั้งค่าระบบ", SettingsRoute::class)
    add(Help::class, "คู่มือการใช้งาน", Help::class)
    add(BulkImport::class, "นำเข้ายาด้วย JSON", null)
    add(LowStock::class, "ยาใกล้หมด", null)
    add(Profile::class, "ข้อมูลส่วนตัว", null)
}

private fun destInfoFor(route: String?): DestInfo {
    val base = route?.substringBefore('/')?.substringBefore('?')
    return base?.let { DEST_INFO[it] } ?: DestInfo("", null)
}

@Composable
fun MainShell(appViewModel: AppViewModel) {
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val nestedNav = rememberNavController()
    val backEntry by nestedNav.currentBackStackEntryAsState()
    val info = destInfoFor(backEntry?.destination?.route)

    val user: TopbarUser? = if (state.userDisplayName.isNotBlank()) {
        TopbarUser(
            initial = state.userInitial.ifBlank { state.userDisplayName.take(1) },
            name = state.userDisplayName,
            role = state.role.name,
        )
    } else {
        null
    }

    AppShell(
        title = info.title,
        items = mainNavItems,
        currentRoute = info.sectionKey ?: "",
        onNavigate = { key ->
            if (key != info.sectionKey) {
                routeForKey(key)?.let { route ->
                    nestedNav.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Sell) { saveState = true }
                    }
                }
            }
        },
        onLogout = appViewModel::signOut,
        pendingSyncCount = state.pendingSyncCount,
        role = state.role,
        user = user,
        onProfileClick = { nestedNav.navigate(Profile) { launchSingleTop = true } },
    ) {
        NavHost(navController = nestedNav, startDestination = Sell) {
            sellNav(nestedNav)
            stockNav(
                nestedNav,
                onOpenReorderSuggestions = { nestedNav.navigate(ReorderSuggestions) { launchSingleTop = true } },
            )
            customersNav(nestedNav)
            salesHistoryNav()
            settingsNav()
            movementsNav()
            suppliersNav(nestedNav)
            importsNav(nestedNav)
            bulkImportNav()
            stockCountsNav(nestedNav)
            expiryNav()
            labelPrintNav()
            planningNav(nestedNav)
            reportsNav(nestedNav)
            kyNav(nestedNav)
            offlineSyncNav()
            helpNav()
            profileNav(nestedNav)
            usersNav(nestedNav)
        }
    }
}
