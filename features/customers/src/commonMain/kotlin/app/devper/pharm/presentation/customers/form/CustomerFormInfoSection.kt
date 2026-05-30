package app.devper.pharm.presentation.customers.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.pricing.Tier
import app.devper.pharm.presentation.customers.CustomerFormFields
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun CustomerFormInfoSection(
    form: CustomerFormFields,
    callbacks: CustomerFormCallbacks,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier, title = "ข้อมูลลูกค้า") {
        CustomerInfoGrid(form = form, callbacks = callbacks)
        AllergyNoteField(value = form.allergyNote, onChange = callbacks.onAllergyNote)
        PriceTierPicker(current = form.priceTier, onPick = callbacks.onPriceTier)
    }
}

@Composable
private fun CustomerInfoGrid(
    form: CustomerFormFields,
    callbacks: CustomerFormCallbacks,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val twoCol = maxWidth >= 560.dp
        if (twoCol) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) { NameField(form, callbacks) }
                Box(modifier = Modifier.weight(1f)) { PhoneField(form, callbacks) }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NameField(form, callbacks)
                PhoneField(form, callbacks)
            }
        }
    }
}

@Composable
private fun NameField(form: CustomerFormFields, callbacks: CustomerFormCallbacks) {
    FormField(label = "ชื่อ-นามสกุล", required = true) {
        PharmTextField(
            value = form.name,
            onValueChange = callbacks.onName,
            placeholder = "เช่น สมศรี ใจดี",
        )
    }
}

@Composable
private fun PhoneField(form: CustomerFormFields, callbacks: CustomerFormCallbacks) {
    FormField(label = "เบอร์โทร") {
        PharmTextField(
            value = form.phone,
            onValueChange = callbacks.onPhone,
            placeholder = "0812345678",
            keyboardType = KeyboardType.Phone,
        )
    }
}

@Composable
private fun AllergyNoteField(value: String, onChange: (String) -> Unit) {
    FormField(label = "แพ้ยา / โรคประจำตัว") {
        Box(modifier = Modifier.heightIn(min = 56.dp, max = 120.dp)) {
            PharmTextField(
                value = value,
                onValueChange = onChange,
                placeholder = "จะถูกแสดงเป็นแถบเตือนสีแดงในตะกร้า",
                singleLine = false,
            )
        }
    }
}

@Composable
private fun PriceTierPicker(
    current: String,
    onPick: (String) -> Unit,
) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "กลุ่มราคา", style = PharmText.h3.copy(color = t.colors.fg2))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TierChip(label = "หน้าร้าน", value = "", current = current, onPick = onPick)
            TierChip(label = "ทั่วไป", value = Tier.Regular, current = current, onPick = onPick)
            TierChip(label = "ส่ง", value = Tier.Wholesale, current = current, onPick = onPick)
        }
    }
}

@Composable
private fun TierChip(
    label: String,
    value: String,
    current: String,
    onPick: (String) -> Unit,
) {
    val t = pharmTokens
    val selected = (value.isEmpty() && (current.isEmpty() || current == Tier.Retail)) ||
        value.equals(current, ignoreCase = true)
    val borderColor = if (selected) t.colors.accent else t.colors.border
    val bg = if (selected) t.colors.accentBgSoft else t.colors.surface
    val fg = if (selected) t.colors.accent else t.colors.fg2

    Row(
        modifier = Modifier
            .clip(t.shapes.pill)
            .background(bg, t.shapes.pill)
            .border(1.dp, borderColor, t.shapes.pill)
            .selectable(selected = selected, role = Role.RadioButton, onClick = { onPick(value) })
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text = label, style = PharmText.body.copy(color = fg))
    }
}

@Composable
internal fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(PaddingValues(horizontal = 20.dp, vertical = 20.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = PharmText.h2)
            if (subtitle != null) {
                Text(text = subtitle, style = PharmText.micro)
            }
        }
        content()
    }
}
