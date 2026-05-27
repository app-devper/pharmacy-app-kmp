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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PharmStatusBadge_Preview() {
    PharmacyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PharmStatus.values().forEach { status ->
                PharmStatusBadge(status = status)
            }
        }
    }
}

@Preview
@Composable
private fun PharmAvatarCircle_Preview() {
    PharmacyTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmAvatarCircle(text = "ภ ปรียา", size = PharmAvatarSize.Sm, tone = PharmBadgeTone.Blue)
            PharmAvatarCircle(text = "ภ ปรียา", size = PharmAvatarSize.Md, tone = PharmBadgeTone.Purple)
            PharmAvatarCircle(text = "Somchai Jaidee", size = PharmAvatarSize.Lg, tone = PharmBadgeTone.Emerald)
            PharmAvatarCircle(text = "?", size = PharmAvatarSize.Md, tone = PharmBadgeTone.Red)
        }
    }
}

@Preview
@Composable
private fun PharmTabBar_Preview() {
    PharmacyTheme {
        var active by remember { mutableStateOf("store") }
        PharmTabBar(
            tabs = listOf(
                PharmTab("store", "ร้านค้า"),
                PharmTab("receipt", "ใบเสร็จ"),
                PharmTab("stock", "สต็อก", count = 3),
                PharmTab("ky", "ขย."),
            ),
            activeId = active,
            onSelect = { active = it },
        )
    }
}

@Preview
@Composable
private fun PharmFilterChips_Multi_Preview() {
    PharmacyTheme {
        val activeIds = remember { mutableStateListOf("import", "sale") }
        Column(modifier = Modifier.padding(16.dp).background(pharmTokens.colors.bgPage)) {
            PharmFilterChips(
                chips = listOf(
                    PharmFilterChip("import", "นำเข้า", count = 12),
                    PharmFilterChip("sale", "ขาย"),
                    PharmFilterChip("return", "คืน"),
                    PharmFilterChip("adjust", "ปรับ", count = 4),
                ),
                activeIds = activeIds.toSet(),
                onToggle = { id ->
                    if (id in activeIds) activeIds.remove(id) else activeIds.add(id)
                },
            )
        }
    }
}

@Preview
@Composable
private fun PharmFilterChips_Single_Preview() {
    PharmacyTheme {
        var active by remember { mutableStateOf<String?>("30") }
        Column(modifier = Modifier.padding(16.dp).background(pharmTokens.colors.bgPage)) {
            PharmSingleSelectChips(
                chips = listOf(
                    PharmFilterChip("30", "30 วัน"),
                    PharmFilterChip("60", "60 วัน"),
                    PharmFilterChip("90", "90 วัน"),
                    PharmFilterChip("180", "180 วัน"),
                    PharmFilterChip("expired", "หมดอายุแล้ว", count = 5),
                ),
                activeId = active,
                onSelect = { active = it },
            )
        }
    }
}

@Preview
@Composable
private fun PharmToggleSwitch_Preview() {
    PharmacyTheme {
        var checked by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PharmToggleSwitch(checked = checked, onCheckedChange = { checked = it })
            PharmToggleSwitch(checked = !checked, onCheckedChange = {})
            PharmToggleSwitch(checked = false, onCheckedChange = {}, enabled = false)
        }
    }
}

@Preview
@Composable
private fun PharmActionMenu_Preview() {
    PharmacyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PharmActionMenu(
                actions = listOf(
                    PharmAction(label = "แก้ไข", onClick = {}, icon = PharmIcons.Pencil, tone = PharmActionTone.Primary),
                    PharmAction(label = "ดู", onClick = {}),
                    PharmAction(label = "ลบ", onClick = {}, icon = PharmIcons.Trash, tone = PharmActionTone.Danger),
                ),
            )
        }
    }
}

@Preview
@Composable
private fun PharmDateRangeField_Preview() {
    PharmacyTheme {
        var range by remember { mutableStateOf(PharmDateRange(fromMillis = 1700000000000L, toMillis = 1705000000000L)) }
        Column(modifier = Modifier.padding(16.dp).background(pharmTokens.colors.bgPage)) {
            PharmDateRangeField(
                range = range,
                onRangeChange = { range = it },
                formatDate = { ms -> "$ms" },
            )
        }
    }
}

private data class SamplePerson(val id: String, val name: String, val role: String, val total: Double)

@Preview
@Composable
private fun PharmTable_Preview() {
    PharmacyTheme {
        val people = listOf(
            SamplePerson("1", "สมชาย ใจดี", "ADMIN", 12500.0),
            SamplePerson("2", "ปรียา ดวงดี", "USER", 8900.0),
            SamplePerson("3", "วิภา เพชรงาม", "USER", 4200.0),
        )
        Box(modifier = Modifier.height(280.dp).fillMaxWidth().background(pharmTokens.colors.bgPage)) {
            PharmTable(
                rows = people,
                columns = listOf(
                    PharmTableColumn(
                        header = "ชื่อ",
                        weight = 2f,
                        cell = { row -> Text(row.name, style = PharmText.body) },
                    ),
                    PharmTableColumn(
                        header = "บทบาท",
                        weight = 1f,
                        cell = { row -> PharmBadge(text = row.role, tone = PharmBadgeTone.Blue) },
                    ),
                    PharmTableColumn(
                        header = "ยอดรวม",
                        weight = 1f,
                        align = PharmColumnAlign.End,
                        cell = { row -> Text(fmtBaht(row.total), style = PharmText.price) },
                    ),
                ),
                key = { it.id },
                bottomRow = {
                    PharmStickyTotalRow(
                        label = "ยอดรวมทั้งหมด",
                        totalText = fmtBaht(people.sumOf { it.total }),
                        subtotalText = "${people.size} รายการ",
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun PharmTable_Empty_Preview() {
    PharmacyTheme {
        Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(pharmTokens.colors.bgPage)) {
            PharmTable<SamplePerson>(
                rows = emptyList(),
                columns = listOf(
                    PharmTableColumn("ชื่อ", cell = { Text(it.name) }),
                ),
                emptyContent = { Text("ไม่พบข้อมูล", style = PharmText.meta) },
            )
        }
    }
}

@Preview
@Composable
private fun PharmMiniBarChart_Preview() {
    PharmacyTheme {
        val data = (1..14).map { day ->
            PharmBarDatum(label = day.toString(), value = (day * 137 % 100).toDouble() + 10)
        }
        Column(modifier = Modifier.padding(16.dp).background(pharmTokens.colors.bgPage)) {
            PharmMiniBarChart(data = data, height = 96.dp)
        }
    }
}

@Preview
@Composable
private fun PharmGroupedBarChart_Preview() {
    PharmacyTheme {
        val revenue = (1..12).map { m -> PharmBarDatum(m.toString(), 10000.0 + m * 1500) }
        val cost = (1..12).map { m -> PharmBarDatum(m.toString(), 6000.0 + m * 900) }
        Column(modifier = Modifier.padding(16.dp).background(pharmTokens.colors.bgPage)) {
            PharmGroupedBarChart(revenue = revenue, cost = cost, height = 120.dp)
        }
    }
}

@Composable
private fun PharmTextField_States_Body() {
    var normal by remember { mutableStateOf("Paracetamol 500mg") }
    var error by remember { mutableStateOf("") }
    var warn by remember { mutableStateOf("ใกล้หมดอายุ") }
    Column(
        modifier = Modifier.padding(16.dp).background(pharmTokens.colors.bgPage),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormField(label = "ชื่อยา", required = true) {
            PharmTextField(value = normal, onValueChange = { normal = it }, placeholder = "ค้นหายา")
        }
        FormField(label = "รหัส", required = true, error = "กรุณากรอกรหัสยา") {
            PharmTextField(
                value = error,
                onValueChange = { error = it },
                isError = true,
                placeholder = "เช่น A001",
            )
        }
        FormField(label = "หมายเหตุ", hint = "ระบุล็อตที่ใกล้หมดอายุ") {
            PharmTextField(value = warn, onValueChange = { warn = it }, isWarning = true)
        }
    }
}

@Preview
@Composable
private fun PharmTextField_States_Preview() {
    PharmacyTheme { PharmTextField_States_Body() }
}

@Preview
@Composable
private fun PharmTextField_States_Dark_Preview() {
    PharmacyTheme(darkTheme = true) { PharmTextField_States_Body() }
}

