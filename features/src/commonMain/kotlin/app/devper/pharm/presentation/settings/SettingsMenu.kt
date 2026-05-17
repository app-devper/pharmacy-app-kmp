package app.devper.pharm.presentation.settings

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.util.UmRoleValidator

enum class SettingsMenuKey {
    Profile,
    Users,
    Movements,
    Imports,
    Suppliers,
    BulkImport,
    StockCounts,
    Expiry,
    LowStock,
    Reorder,
    Reports,
    Profit,
    Eod,
    Ky9,
    Ky10,
    Ky11,
    Ky12,
    OfflineSync,
    Help,
}

enum class SettingsMenuGroup(val label: String) {
    Self("บัญชีของฉัน"),
    Inventory("สต็อกและสินค้า"),
    Compliance("รายงาน ขย."),
    Reports("รายงานยอดขาย"),
    Admin("จัดการระบบ"),
    Help("ช่วยเหลือ"),
}

data class SettingsMenuItem(
    val key: SettingsMenuKey,
    val label: String,
    val group: SettingsMenuGroup,
    val visibleTo: (Role) -> Boolean = { true },
)

object SettingsMenuRegistry {

    private val items: List<SettingsMenuItem> = listOf(
        SettingsMenuItem(SettingsMenuKey.Profile, "ข้อมูลส่วนตัว", SettingsMenuGroup.Self),
        SettingsMenuItem(SettingsMenuKey.Users, "ผู้ใช้งาน", SettingsMenuGroup.Self,
            visibleTo = UmRoleValidator::canViewUsers),

        SettingsMenuItem(SettingsMenuKey.Movements, "ประวัติเคลื่อนไหวสต็อก", SettingsMenuGroup.Inventory),
        SettingsMenuItem(SettingsMenuKey.Imports, "บิลรับสินค้า", SettingsMenuGroup.Inventory),
        SettingsMenuItem(SettingsMenuKey.Suppliers, "ผู้จัดจำหน่าย", SettingsMenuGroup.Inventory),
        SettingsMenuItem(SettingsMenuKey.BulkImport, "นำเข้ายาด้วย JSON", SettingsMenuGroup.Inventory,
            visibleTo = UmRoleValidator::canManageUsers),
        SettingsMenuItem(SettingsMenuKey.StockCounts, "นับสต็อก", SettingsMenuGroup.Inventory),
        SettingsMenuItem(SettingsMenuKey.Expiry, "ล็อตใกล้หมดอายุ", SettingsMenuGroup.Inventory),
        SettingsMenuItem(SettingsMenuKey.LowStock, "ยาใกล้หมด", SettingsMenuGroup.Inventory),
        SettingsMenuItem(SettingsMenuKey.Reorder, "คำแนะนำสั่งซื้อ", SettingsMenuGroup.Inventory),

        SettingsMenuItem(SettingsMenuKey.Reports, "รายงานสรุป", SettingsMenuGroup.Reports),
        SettingsMenuItem(SettingsMenuKey.Profit, "กำไรต่อยา", SettingsMenuGroup.Reports),
        SettingsMenuItem(SettingsMenuKey.Eod, "ปิดยอดสิ้นวัน", SettingsMenuGroup.Reports),

        SettingsMenuItem(SettingsMenuKey.Ky9, "ขย.9", SettingsMenuGroup.Compliance),
        SettingsMenuItem(SettingsMenuKey.Ky10, "ขย.10", SettingsMenuGroup.Compliance),
        SettingsMenuItem(SettingsMenuKey.Ky11, "ขย.11", SettingsMenuGroup.Compliance),
        SettingsMenuItem(SettingsMenuKey.Ky12, "ขย.12", SettingsMenuGroup.Compliance),

        SettingsMenuItem(SettingsMenuKey.OfflineSync, "บิลค้างซิงก์", SettingsMenuGroup.Admin),

        SettingsMenuItem(SettingsMenuKey.Help, "คู่มือการใช้งาน", SettingsMenuGroup.Help),
    )

    fun groupsFor(role: Role): List<Pair<SettingsMenuGroup, List<SettingsMenuItem>>> =
        items.filter { it.visibleTo(role) }
            .groupBy { it.group }
            .toList()
            .sortedBy { (group, _) -> group.ordinal }
}
