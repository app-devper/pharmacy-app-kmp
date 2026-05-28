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

@Composable
private fun PharmDarkPreview(content: @Composable () -> Unit) {
    PharmacyTheme(darkTheme = true) {
        Box(modifier = Modifier.background(pharmTokens.colors.bgPage)) {
            content()
        }
    }
}

@Composable
private fun PharmLightPreview(content: @Composable () -> Unit) {
    PharmacyTheme(darkTheme = false) {
        Box(modifier = Modifier.background(pharmTokens.colors.bgPage)) {
            content()
        }
    }
}

@Composable
private fun PharmStatusBadge_Body() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PharmStatus.values().forEach { status ->
            PharmStatusBadge(status = status)
        }
    }
}

@Preview
@Composable
private fun PharmStatusBadge_Preview() {
    PharmLightPreview { PharmStatusBadge_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmStatusBadge_Dark_Preview() {
    PharmDarkPreview { PharmStatusBadge_Body() }
}

@Composable
private fun PharmAvatarCircle_Body() {
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

@Preview
@Composable
private fun PharmAvatarCircle_Preview() {
    PharmLightPreview { PharmAvatarCircle_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmAvatarCircle_Dark_Preview() {
    PharmDarkPreview { PharmAvatarCircle_Body() }
}

@Composable
private fun PharmTabBar_Body() {
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

@Preview
@Composable
private fun PharmTabBar_Preview() {
    PharmLightPreview { PharmTabBar_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmTabBar_Dark_Preview() {
    PharmDarkPreview { PharmTabBar_Body() }
}

@Composable
private fun PharmFilterChips_Multi_Body() {
    val activeIds = remember { mutableStateListOf("import", "sale") }
    Column(modifier = Modifier.padding(16.dp)) {
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

@Preview
@Composable
private fun PharmFilterChips_Multi_Preview() {
    PharmLightPreview { PharmFilterChips_Multi_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmFilterChips_Multi_Dark_Preview() {
    PharmDarkPreview { PharmFilterChips_Multi_Body() }
}

@Composable
private fun PharmFilterChips_Single_Body() {
    var active by remember { mutableStateOf<String?>("30") }
    Column(modifier = Modifier.padding(16.dp)) {
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

@Preview
@Composable
private fun PharmFilterChips_Single_Preview() {
    PharmLightPreview { PharmFilterChips_Single_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmFilterChips_Single_Dark_Preview() {
    PharmDarkPreview { PharmFilterChips_Single_Body() }
}

@Composable
private fun PharmToggleSwitch_Body() {
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

@Preview
@Composable
private fun PharmToggleSwitch_Preview() {
    PharmLightPreview { PharmToggleSwitch_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmToggleSwitch_Dark_Preview() {
    PharmDarkPreview { PharmToggleSwitch_Body() }
}

@Composable
private fun PharmActionMenu_Body() {
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

@Preview
@Composable
private fun PharmActionMenu_Preview() {
    PharmLightPreview { PharmActionMenu_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmActionMenu_Dark_Preview() {
    PharmDarkPreview { PharmActionMenu_Body() }
}

@Composable
private fun PharmDateRangeField_Body() {
    var range by remember { mutableStateOf(PharmDateRange(fromMillis = 1700000000000L, toMillis = 1705000000000L)) }
    Column(modifier = Modifier.padding(16.dp)) {
        PharmDateRangeField(
            range = range,
            onRangeChange = { range = it },
            formatDate = { ms -> "$ms" },
        )
    }
}

@Preview
@Composable
private fun PharmDateRangeField_Preview() {
    PharmLightPreview { PharmDateRangeField_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmDateRangeField_Dark_Preview() {
    PharmDarkPreview { PharmDateRangeField_Body() }
}

private data class SamplePerson(val id: String, val name: String, val role: String, val total: Double)

@Composable
private fun PharmTable_Body() {
    val people = listOf(
        SamplePerson("1", "สมชาย ใจดี", "ADMIN", 12500.0),
        SamplePerson("2", "ปรียา ดวงดี", "USER", 8900.0),
        SamplePerson("3", "วิภา เพชรงาม", "USER", 4200.0),
    )
    Box(modifier = Modifier.height(280.dp).fillMaxWidth()) {
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

@Preview
@Composable
private fun PharmTable_Preview() {
    PharmLightPreview { PharmTable_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmTable_Dark_Preview() {
    PharmDarkPreview { PharmTable_Body() }
}

@Composable
private fun PharmTable_Empty_Body() {
    Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
        PharmTable<SamplePerson>(
            rows = emptyList(),
            columns = listOf(
                PharmTableColumn("ชื่อ", cell = { Text(it.name) }),
            ),
            emptyContent = { Text("ไม่พบข้อมูล", style = PharmText.meta) },
        )
    }
}

@Preview
@Composable
private fun PharmTable_Empty_Preview() {
    PharmLightPreview { PharmTable_Empty_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmTable_Empty_Dark_Preview() {
    PharmDarkPreview { PharmTable_Empty_Body() }
}

@Composable
private fun PharmMiniBarChart_Body() {
    val data = (1..14).map { day ->
        PharmBarDatum(label = day.toString(), value = (day * 137 % 100).toDouble() + 10)
    }
    Column(modifier = Modifier.padding(16.dp)) {
        PharmMiniBarChart(data = data, height = 96.dp)
    }
}

@Preview
@Composable
private fun PharmMiniBarChart_Preview() {
    PharmLightPreview { PharmMiniBarChart_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmMiniBarChart_Dark_Preview() {
    PharmDarkPreview { PharmMiniBarChart_Body() }
}

@Composable
private fun PharmGroupedBarChart_Body() {
    val revenue = (1..12).map { m -> PharmBarDatum(m.toString(), 10000.0 + m * 1500) }
    val cost = (1..12).map { m -> PharmBarDatum(m.toString(), 6000.0 + m * 900) }
    Column(modifier = Modifier.padding(16.dp)) {
        PharmGroupedBarChart(revenue = revenue, cost = cost, height = 120.dp)
    }
}

@Preview
@Composable
private fun PharmGroupedBarChart_Preview() {
    PharmLightPreview { PharmGroupedBarChart_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmGroupedBarChart_Dark_Preview() {
    PharmDarkPreview { PharmGroupedBarChart_Body() }
}

@Composable
private fun PharmBadge_AllTones_Body() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PharmBadgeTone.values().forEach { tone ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PharmBadge(text = tone.name, tone = tone, size = PharmBadgeSize.Sm)
                PharmBadge(text = tone.name, tone = tone, size = PharmBadgeSize.Md)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            KyBadge(form = 9)
            KyBadge(form = 10)
            KyBadge(form = 11)
            KyBadge(form = 12)
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
    PharmLightPreview { PharmTextField_States_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmTextField_States_Dark_Preview() {
    PharmDarkPreview { PharmTextField_States_Body() }
}

@Preview
@Composable
private fun PharmBadge_AllTones_Preview() {
    PharmLightPreview { PharmBadge_AllTones_Body() }
}

@Preview(name = "dark")
@Composable
private fun PharmBadge_AllTones_Dark_Preview() {
    PharmDarkPreview { PharmBadge_AllTones_Body() }
}

@Composable
private fun MetricCard_AllTints_Body() {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetricCard(label = "ยอดขายวันนี้",  value = fmtBaht(28450.0), sub = "วันก่อน +12%", tint = MetricTint.Blue)
        MetricCard(label = "บิลทั้งหมด",     value = "342",            sub = "30 วันที่ผ่านมา", tint = MetricTint.Indigo)
        MetricCard(label = "ยาที่หมด",       value = "5 รายการ",        sub = "ต้องสั่งเพิ่ม", tint = MetricTint.Green)
        MetricCard(label = "ลูกค้า VIP",     value = "18",             sub = "+2 รายใหม่", tint = MetricTint.Purple)
    }
}

@Preview
@Composable
private fun MetricCard_Preview() {
    PharmLightPreview { MetricCard_AllTints_Body() }
}

@Preview(name = "dark")
@Composable
private fun MetricCard_Dark_Preview() {
    PharmDarkPreview { MetricCard_AllTints_Body() }
}

@Composable
private fun DrugCard_Body() {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DrugCard(
            name = "Paracetamol 500 mg",
            generic = "Paracetamol",
            price = 12.50,
            stock = 240,
            unit = "เม็ด",
            onClick = {},
            type = DrugCardType.Rx,
            altUnitCount = 2,
            kyForm = 10,
        )
        DrugCard(
            name = "Vitamin C 1000 mg",
            generic = "Ascorbic acid",
            price = 95.0,
            stock = 12,
            unit = "เม็ด",
            onClick = {},
            type = DrugCardType.Supplement,
        )
        DrugCard(
            name = "ใบมะกรูดสกัด",
            generic = null,
            price = 180.0,
            stock = 0,
            unit = "ขวด",
            onClick = {},
            type = DrugCardType.Herb,
            kyForm = 9,
        )
    }
}

@Preview
@Composable
private fun DrugCard_Preview() {
    PharmLightPreview { DrugCard_Body() }
}

@Preview(name = "dark")
@Composable
private fun DrugCard_Dark_Preview() {
    PharmDarkPreview { DrugCard_Body() }
}
