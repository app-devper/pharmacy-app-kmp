package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

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
