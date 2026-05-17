package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
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
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.reports.Profit
import app.devper.pharm.presentation.reports.Reports
import app.devper.pharm.presentation.saleshistory.SalesHistory
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.settings.Settings as SettingsRoute
import app.devper.pharm.presentation.stock.Stock
import app.devper.pharm.presentation.stockcount.StockCounts
import app.devper.pharm.presentation.suppliers.Suppliers
import app.devper.pharm.presentation.users.Users

private val MAIN_NAV_ROUTES: List<Any> = listOf(
    Sell,
    SalesHistory,
    Stock,
    StockCounts,
    Expiry,
    LabelPrint,
    Movements,
    OfflineSync,
    Imports,
    Suppliers,
    Customers,
    Reports,
    Profit,
    Ky9,
    Users,
    SettingsRoute,
    Help,
)

internal val MAIN_NAV: List<NavItem> = listOf(
    NavItem(route = Sell::class.qualifiedName!!,         label = "หน้าขายยา",          icon = PharmIcons.Sell),
    NavItem(route = SalesHistory::class.qualifiedName!!, label = "ประวัติการขาย",      icon = PharmIcons.SalesHistory),
    NavItem(route = Stock::class.qualifiedName!!,        label = "สต็อกยา",            icon = PharmIcons.Stock),
    NavItem(route = StockCounts::class.qualifiedName!!,  label = "ตรวจนับสต็อก",       icon = PharmIcons.StockCount, admin = true),
    NavItem(route = Expiry::class.qualifiedName!!,       label = "จัดการวันหมดอายุ",   icon = PharmIcons.Expiry,     admin = true),
    NavItem(route = LabelPrint::class.qualifiedName!!,   label = "พิมพ์ฉลาก",          icon = PharmIcons.Print,      admin = true),
    NavItem(route = Movements::class.qualifiedName!!,    label = "ความเคลื่อนไหวสต็อก", icon = PharmIcons.Movements),
    NavItem(route = OfflineSync::class.qualifiedName!!,  label = "รายการค้างซิงค์",    icon = PharmIcons.OfflineSync),
    NavItem(route = Imports::class.qualifiedName!!,      label = "นำเข้าสินค้า",       icon = PharmIcons.Imports,    admin = true),
    NavItem(route = Suppliers::class.qualifiedName!!,    label = "ซัพพลายเออร์",       icon = PharmIcons.Suppliers,  admin = true),
    NavItem(route = Customers::class.qualifiedName!!,    label = "ลูกค้า",             icon = PharmIcons.Customers),
    NavItem(route = Reports::class.qualifiedName!!,      label = "รายงาน",             icon = PharmIcons.Reports),
    NavItem(route = Profit::class.qualifiedName!!,       label = "กำไร",               icon = PharmIcons.Profit,     admin = true),
    NavItem(route = Ky9::class.qualifiedName!!,          label = "แบบฟอร์ม ขย. 9–12",  icon = PharmIcons.KyForms,    admin = true),
    NavItem(route = Users::class.qualifiedName!!,        label = "จัดการผู้ใช้งาน",    icon = PharmIcons.Users,      admin = true),
    NavItem(route = SettingsRoute::class.qualifiedName!!, label = "ตั้งค่าระบบ",        icon = PharmIcons.Settings,   admin = true),
    NavItem(route = Help::class.qualifiedName!!,         label = "คู่มือการใช้งาน",    icon = PharmIcons.Help),
)

internal val LOGOUT_ITEM = NavItem(
    route = "logout",
    label = "ออก",
    icon = PharmIcons.Logout,
)

@Composable
internal fun ShelledScreen(
    title: String,
    currentRoute: String,
    navController: NavController,
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

            val typedRoute = MAIN_NAV_ROUTES.firstOrNull { it::class.qualifiedName == destKey }
                ?: return@AppShell
            navController.navigate(typedRoute) {

                launchSingleTop = true
                restoreState = true
                popUpTo(Sell) { saveState = true }
            }
        },
        onLogout = onLogout,
        logoutItem = LOGOUT_ITEM,
        pendingSyncCount = pendingSyncCount,
        role = role,
        user = user,
        onProfileClick = { navController.navigate(Profile) { launchSingleTop = true } },
        content = content,
    )
}
