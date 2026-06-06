package app.devper.pharm.presentation.customers.form

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.presentation.customers.CustomerFormFields
import androidx.compose.ui.Alignment
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmHelpHint
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun CustomerFormInfoSection(
    form: CustomerFormFields,
    callbacks: CustomerFormCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmFormCard(modifier = modifier, title = "ข้อมูลลูกค้า") {
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

private const val TIER_RETAIL_ID = "retail"

private val priceTierChips = listOf(
    PharmFilterChip(id = TIER_RETAIL_ID, label = "หน้าร้าน"),
    PharmFilterChip(id = Tier.Regular, label = "ทั่วไป"),
    PharmFilterChip(id = Tier.Wholesale, label = "ส่ง"),
)

@Composable
private fun PriceTierPicker(
    current: String,
    onPick: (String) -> Unit,
) {
    val t = pharmTokens
    val activeId = when {
        current.isEmpty() || current.equals(Tier.Retail, ignoreCase = true) -> TIER_RETAIL_ID
        current.equals(Tier.Regular, ignoreCase = true) -> Tier.Regular
        current.equals(Tier.Wholesale, ignoreCase = true) -> Tier.Wholesale
        else -> TIER_RETAIL_ID
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = "กลุ่มราคา", style = PharmText.h3.copy(color = t.colors.fg2))
            PharmHelpHint(
                text = "กลุ่มราคาเริ่มต้นของลูกค้า เวลาขายระบบจะใช้ราคาตามกลุ่มนี้ ถ้ายาไม่มีราคากลุ่มนั้นจะใช้ราคาหน้าร้านแทน",
            )
        }
        PharmSingleSelectChips(
            chips = priceTierChips,
            activeId = activeId,
            onSelect = { id -> onPick(if (id == TIER_RETAIL_ID) "" else id) },
            scrollable = false,
        )
    }
}
