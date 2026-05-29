package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun ImportDrugPickerDialog(
    drugs: List<Drug>,
    onDismiss: () -> Unit,
    onPick: (Drug) -> Unit,
) {
    val t = pharmTokens
    var query by remember { mutableStateOf("") }
    val visible = remember(query, drugs) {
        if (query.isBlank()) drugs
        else {
            val q = query.trim().lowercase()
            drugs.filter { d ->
                d.name.lowercase().contains(q) ||
                    d.barcode?.lowercase()?.contains(q) == true ||
                    d.genericName?.lowercase()?.contains(q) == true
            }
        }
    }
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "เลือกยา",
        size = PharmModalSize.Lg,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onDismiss,
                variant = PharmButtonVariant.Ghost,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            PharmTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "ค้นหาชื่อ / barcode",
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
                trailingSlot = if (query.isNotEmpty()) {
                    {
                        Icon(
                            PharmIcons.Close,
                            contentDescription = "ล้าง",
                            tint = t.colors.fgMuted,
                            modifier = Modifier.size(18.dp).clickable { query = "" },
                        )
                    }
                } else null,
                modifier = Modifier.fillMaxWidth(),
            )
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(visible, key = { it.id }) { drug ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(t.shapes.md)
                                .background(t.colors.surface, t.shapes.md)
                                .clickable { onPick(drug) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(drug.name, style = PharmText.body.copy(color = t.colors.fg1))
                            val sub = listOfNotNull(
                                drug.genericName,
                                drug.unit,
                                drug.barcode,
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
