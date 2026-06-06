package app.devper.pharm.ui.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

private val SidebarRailWidth: Dp = 64.dp

data class SidebarNavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val admin: Boolean = false,
)

val DefaultPharmNav: List<SidebarNavItem> = listOf(
    SidebarNavItem("sell",         PharmIcons.Sell,         "หน้าขายยา"),
    SidebarNavItem("salesHistory", PharmIcons.SalesHistory, "ประวัติการขาย"),
    SidebarNavItem("stock",        PharmIcons.Stock,        "สต็อกยา"),
    SidebarNavItem("stockCount",   PharmIcons.StockCount,   "ตรวจนับสต็อก",      admin = true),
    SidebarNavItem("expiry",       PharmIcons.Expiry,       "จัดการวันหมดอายุ",  admin = true),
    SidebarNavItem("movements",    PharmIcons.Movements,    "ความเคลื่อนไหวสต็อก"),
    SidebarNavItem("offlineSync",  PharmIcons.OfflineSync,  "รายการค้างซิงค์"),
    SidebarNavItem("imports",      PharmIcons.Imports,      "นำเข้าสินค้า",      admin = true),
    SidebarNavItem("suppliers",    PharmIcons.Suppliers,    "ซัพพลายเออร์",      admin = true),
    SidebarNavItem("customers",    PharmIcons.Customers,    "ลูกค้า"),
    SidebarNavItem("reports",      PharmIcons.Reports,      "รายงาน"),
    SidebarNavItem("profit",       PharmIcons.Profit,       "กำไร",              admin = true),
    SidebarNavItem("kyforms",      PharmIcons.KyForms,      "แบบฟอร์ม ขย. 9–12", admin = true),
    SidebarNavItem("users",        PharmIcons.Users,        "จัดการผู้ใช้งาน",   admin = true),
    SidebarNavItem("settings",     PharmIcons.Settings,     "ตั้งค่าระบบ",       admin = true),
    SidebarNavItem("help",         PharmIcons.Help,         "คู่มือการใช้งาน"),
)

@Composable
fun PharmSidebar(
    activeId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<SidebarNavItem> = DefaultPharmNav,
    collapsed: Boolean = false,
    onToggleCollapse: (() -> Unit)? = null,
    online: Boolean = true,
    versionLabel: String = "v3.2.1",
) {
    val t = pharmTokens
    val width by animateDpAsState(if (collapsed) SidebarRailWidth else t.dimens.sidebarWidth)
    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(t.colors.sidebarBg),
    ) {

        BrandHeader(collapsed = collapsed)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(items, key = { it.id }) { item ->
                SidebarRow(
                    item = item,
                    active = item.id == activeId,
                    collapsed = collapsed,
                    onClick = { onSelect(item.id) },
                )
            }
        }

        SidebarFooter(
            collapsed = collapsed,
            online = online,
            versionLabel = versionLabel,
            onToggleCollapse = onToggleCollapse,
        )
    }
}

@Composable
private fun BrandHeader(collapsed: Boolean) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (collapsed) 0.dp else 16.dp, vertical = 16.dp),
        horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(t.shapes.md)
                .background(
                    Brush.linearGradient(
                        colors = listOf(t.colors.accent, t.colors.accentHover)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = PharmIcons.Pill,
                contentDescription = null,
                tint = t.colors.sidebarFg,
                modifier = Modifier.size(18.dp),
            )
        }
        if (!collapsed) {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    text = "ร้านยา เฮลท์ตี้ฟาร์ม",
                    style = PharmText.body.copy(
                        color = t.colors.sidebarFg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                )
                Text(
                    text = "ระบบ POS ร้านขายยา",
                    style = PharmText.micro.copy(color = t.colors.sidebarFgMuted),
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(t.colors.sidebarItemHover),
    )
}

@Composable
private fun SidebarRow(
    item: SidebarNavItem,
    active: Boolean,
    collapsed: Boolean,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    val bg = if (active) t.colors.sidebarItemActive else Color.Transparent
    val fg = if (active) t.colors.sidebarFg else t.colors.sidebarFgMuted
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(t.shapes.md)
            .background(bg)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {
                role = Role.Tab
                selected = active
            }
            .padding(horizontal = if (collapsed) 0.dp else 12.dp, vertical = 8.dp),
        horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = if (collapsed) item.label else null,
            tint = fg,
            modifier = Modifier.size(18.dp),
        )
        if (!collapsed) {
            Text(
                text = item.label,
                style = PharmText.body.copy(color = fg),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (item.admin && !active) {
                Text(
                    text = "ADMIN",
                    style = PharmText.micro.copy(
                        color = t.colors.sidebarFgMuted,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SidebarFooter(
    collapsed: Boolean,
    online: Boolean,
    versionLabel: String,
    onToggleCollapse: (() -> Unit)?,
) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(t.colors.sidebarItemHover),
    )
    if (onToggleCollapse != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onToggleCollapse,
                    role = Role.Button,
                )
                .padding(horizontal = if (collapsed) 0.dp else 16.dp, vertical = 12.dp),
            horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = PharmIcons.Hamburger,
                contentDescription = if (collapsed) "ขยายเมนู" else "ย่อเมนู",
                tint = t.colors.sidebarFgMuted,
                modifier = Modifier.size(18.dp),
            )
            if (!collapsed) {
                Text(
                    text = "ย่อเมนู",
                    style = PharmText.micro.copy(color = t.colors.sidebarFgMuted),
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(t.shapes.pill)
                    .background(if (online) t.colors.successFg else t.colors.fgMuted),
            )
            Text(
                text = if (online) "ออนไลน์ · $versionLabel" else "ออฟไลน์ · $versionLabel",
                style = PharmText.micro.copy(color = t.colors.sidebarFgMuted),
            )
        }
    }
}
