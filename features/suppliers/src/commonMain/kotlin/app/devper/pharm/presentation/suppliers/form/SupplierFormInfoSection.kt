package app.devper.pharm.presentation.suppliers.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import app.devper.pharm.presentation.suppliers.SupplierFormFields
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun SupplierFormInfoSection(
    form: SupplierFormFields,
    callbacks: SupplierFormCallbacks,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier, title = "ข้อมูลผู้จัดจำหน่าย") {
        SupplierInfoGrid(form = form, callbacks = callbacks)
        AddressField(value = form.address, onChange = callbacks.onAddress)
        NotesField(value = form.notes, onChange = callbacks.onNotes)
    }
}

@Composable
private fun SupplierInfoGrid(
    form: SupplierFormFields,
    callbacks: SupplierFormCallbacks,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val twoCol = maxWidth >= 560.dp
        if (twoCol) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GridRow(
                    left = { NameField(form, callbacks) },
                    right = { ContactNameField(form, callbacks) },
                )
                GridRow(
                    left = { PhoneField(form, callbacks) },
                    right = { TaxIdField(form, callbacks) },
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NameField(form, callbacks)
                ContactNameField(form, callbacks)
                PhoneField(form, callbacks)
                TaxIdField(form, callbacks)
            }
        }
    }
}

@Composable
private fun GridRow(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}

@Composable
private fun NameField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
    FormField(label = "ชื่อบริษัท / ผู้จัดจำหน่าย", required = true) {
        PharmTextField(
            value = form.name,
            onValueChange = callbacks.onName,
            placeholder = "เช่น บริษัท เอ บี ซี ฟาร์มา จำกัด",
        )
    }
}

@Composable
private fun ContactNameField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
    FormField(label = "ผู้ติดต่อ") {
        PharmTextField(
            value = form.contactName,
            onValueChange = callbacks.onContactName,
            placeholder = "ชื่อพนักงานขาย",
        )
    }
}

@Composable
private fun PhoneField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
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
private fun TaxIdField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
    FormField(label = "เลขประจำตัวผู้เสียภาษี") {
        PharmTextField(
            value = form.taxId,
            onValueChange = callbacks.onTaxId,
            placeholder = "13 หลัก",
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun AddressField(value: String, onChange: (String) -> Unit) {
    FormField(label = "ที่อยู่") {
        Box(modifier = Modifier.heightIn(min = 56.dp, max = 120.dp)) {
            PharmTextField(
                value = value,
                onValueChange = onChange,
                placeholder = "บ้านเลขที่ / ถนน / ตำบล / อำเภอ / จังหวัด",
                singleLine = false,
            )
        }
    }
}

@Composable
private fun NotesField(value: String, onChange: (String) -> Unit) {
    FormField(label = "หมายเหตุ") {
        Box(modifier = Modifier.heightIn(min = 56.dp, max = 120.dp)) {
            PharmTextField(
                value = value,
                onValueChange = onChange,
                placeholder = "เงื่อนไขการสั่งซื้อ / ส่วนลด / รายละเอียดเพิ่มเติม",
                singleLine = false,
            )
        }
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
