package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.components.AppShell
import app.devper.pharm.ui.components.NavItem
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.TopbarUser
import app.devper.pharm.presentation.customers.Customers
import app.devper.pharm.presentation.expiry.Expiry
import app.devper.pharm.presentation.help.Help
import app.devper.pharm.presentation.imports.Imports
import app.devper.pharm.presentation.ky.Ky9
import app.devper.pharm.presentation.labels.LabelPrint
import app.devper.pharm.presentation.movements.Movements
import app.devper.pharm.presentation.offlinesync.OfflineSync
import app.devper.pharm.presentation.reports.Profit
import app.devper.pharm.presentation.reports.Reports
import app.devper.pharm.presentation.saleshistory.SalesHistory
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.settings.Settings as SettingsRoute
import app.devper.pharm.presentation.stock.Stock
import app.devper.pharm.presentation.stockcount.StockCounts
import app.devper.pharm.presentation.suppliers.Suppliers
import app.devper.pharm.presentation.users.Users

private fun routeKey(route: Any): String =
    requireNotNull(route::class.qualifiedName) { "main nav route ${route::class.simpleName} must have a qualified name" }

private data class MainNavEntry(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val admin: Boolean = false,
)

private val MAIN_NAV_TABLE: List<MainNavEntry> = listOf(
    MainNavEntry(Sell,         "หน้าขายยา",          PharmIcons.Sell),
    MainNavEntry(SalesHistory, "ประวัติการขาย",      PharmIcons.SalesHistory),
    MainNavEntry(Stock,        "สต็อกยา",            PharmIcons.Stock),
    MainNavEntry(StockCounts,  "ตรวจนับสต็อก",       PharmIcons.StockCount, admin = true),
    MainNavEntry(Expiry,       "จัดการวันหมดอายุ",   PharmIcons.Expiry,     admin = true),
    MainNavEntry(LabelPrint,   "พิมพ์ฉลาก",          PharmIcons.Print,      admin = true),
    MainNavEntry(Movements,    "ความเคลื่อนไหวสต็อก", PharmIcons.Movements),
    MainNavEntry(OfflineSync,  "รายการค้างซิงค์",    PharmIcons.OfflineSync),
    MainNavEntry(Imports,      "นำเข้าสินค้า",       PharmIcons.Imports,    admin = true),
    MainNavEntry(Suppliers,    "ซัพพลายเออร์",       PharmIcons.Suppliers,  admin = true),
    MainNavEntry(Customers,    "ลูกค้า",             PharmIcons.Customers),
    MainNavEntry(Reports,      "รายงาน",             PharmIcons.Reports),
    MainNavEntry(Profit,       "กำไร",               PharmIcons.Profit,     admin = true),
    MainNavEntry(Ky9,          "แบบฟอร์ม ขย. 9–12",  PharmIcons.KyForms,    admin = true),
    MainNavEntry(Users,        "จัดการผู้ใช้งาน",    PharmIcons.Users,      admin = true),
    MainNavEntry(SettingsRoute, "ตั้งค่าระบบ",        PharmIcons.Settings,   admin = true),
    MainNavEntry(Help,         "คู่มือการใช้งาน",    PharmIcons.Help),
)

private val MAIN_NAV: List<NavItem> = MAIN_NAV_TABLE.map { entry ->
    NavItem(route = routeKey(entry.route), label = entry.label, icon = entry.icon, admin = entry.admin)
}

@Composable
fun ShelledScreen(
    title: String,
    currentRoute: String,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    content: @Composable () -> Unit,
) {
    AppShell(
        title = title,
        items = MAIN_NAV,
        currentRoute = currentRoute,
        onNavigate = { destKey ->
            if (destKey == currentRoute) return@AppShell

            val typedRoute = MAIN_NAV_TABLE.firstOrNull { routeKey(it.route) == destKey }?.route
                ?: return@AppShell
            onNavigateMain(typedRoute)
        },
        onLogout = onLogout,
        pendingSyncCount = pendingSyncCount,
        role = role,
        user = user,
        onProfileClick = onProfileClick,
        content = content,
    )
}
