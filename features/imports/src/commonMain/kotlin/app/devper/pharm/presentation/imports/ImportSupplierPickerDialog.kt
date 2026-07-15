package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

@Composable
fun ImportSupplierPickerDialog(
    suppliers: List<Supplier>,
    onDismiss: () -> Unit,
    onPick: (Supplier) -> Unit,
) {
    val t = pharmTokens
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
    val s = pharmStrings
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = s.importsFormSupplierPickerTitle,
        size = PharmModalSize.Lg,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onDismiss,
                variant = PharmButtonVariant.Ghost,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            PharmTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = s.importsFormSupplierSearchPlaceholder,
                singleLine = true,
                imeAction = ImeAction.Search,
                leadingSlot = {
                    Icon(
                        PharmIcons.Search,
                        contentDescription = null,
                        tint = t.colors.fgMuted,
                        modifier = Modifier.size(18.dp),
                    )
                },
                onClear = { query = "" },
                modifier = Modifier.fillMaxWidth(),
            )
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(visible, key = { it.id }) { supplier ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(t.shapes.md)
                                .background(t.colors.surface, t.shapes.md)
                                .pharmClickable(role = Role.Button) { onPick(supplier) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(supplier.name, style = PharmText.body.copy(color = t.colors.fg1))
                            val sub = listOfNotNull(
                                supplier.contactName.takeIf { it.isNotBlank() },
                                supplier.phone.takeIf { it.isNotBlank() },
                            ).joinToString(" · ")
                            if (sub.isNotBlank()) {
                                Text(
                                    text = sub,
                                    style = PharmText.micro.copy(color = t.colors.fg2),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
