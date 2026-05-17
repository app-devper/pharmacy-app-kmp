package app.devper.pharm.presentation.imports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Supplier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportSupplierPickerDialog(
    suppliers: List<Supplier>,
    onDismiss: () -> Unit,
    onPick: (Supplier) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val visible = remember(query, suppliers) {
        if (query.isBlank()) suppliers
        else {
            val q = query.trim().lowercase()
            suppliers.filter { s ->
                s.name.lowercase().contains(q) ||
                    s.contactName.lowercase().contains(q) ||
                    s.phone.lowercase().contains(q)
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("เลือกผู้จัดจำหน่าย") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("ค้นหาชื่อ / ผู้ติดต่อ / เบอร์โทร") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "ล้าง")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                )
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(visible, key = { it.id }) { supplier ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPick(supplier) },
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                    Text(supplier.name, style = MaterialTheme.typography.bodyLarge)
                                    val sub = listOfNotNull(
                                        supplier.contactName.takeIf { it.isNotBlank() },
                                        supplier.phone.takeIf { it.isNotBlank() },
                                    ).joinToString(" · ")
                                    if (sub.isNotBlank()) {
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("ยกเลิก") }
        },
    )
}
