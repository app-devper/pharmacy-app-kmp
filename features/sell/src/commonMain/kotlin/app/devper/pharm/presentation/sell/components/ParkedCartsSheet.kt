package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.tabular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkedCartsSheet(
    slots: List<ParkedCart?>,
    canParkActiveCart: Boolean,
    onTapSlot: (Int) -> Unit,
    onDiscardSlot: (Int) -> Unit,
    onRequestOverwrite: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "บิลที่พัก",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Text(
                text = if (canParkActiveCart) {
                    "เลือกช่องว่างเพื่อพักบิลปัจจุบัน หรือกดบิลที่พักไว้เพื่อเรียกคืน"
                } else {
                    "กดบิลที่พักไว้เพื่อเรียกคืน — ตะกร้าตอนนี้ว่าง"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(4.dp))
            slots.forEachIndexed { idx, parked ->
                if (parked == null) {
                    EmptySlotRow(
                        slotNumber = idx + 1,
                        canPark = canParkActiveCart,
                        onClick = { onTapSlot(idx) },
                    )
                } else {
                    FilledSlotRow(
                        slotNumber = idx + 1,
                        parked = parked,
                        onRestore = { onTapSlot(idx) },
                        onOverwrite = { onRequestOverwrite(idx) },
                        canOverwrite = canParkActiveCart,
                        onDiscard = { onDiscardSlot(idx) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySlotRow(
    slotNumber: Int,
    canPark: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canPark, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SlotBadge(slotNumber, dimmed = !canPark)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (canPark) "พักบิลที่ช่องนี้" else "ว่าง",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (canPark) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FilledSlotRow(
    slotNumber: Int,
    parked: ParkedCart,
    onRestore: () -> Unit,
    onOverwrite: () -> Unit,
    canOverwrite: Boolean,
    onDiscard: () -> Unit,
) {
    var confirmingDiscard by remember { mutableStateOf(false) }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRestore),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SlotBadge(slotNumber)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = parked.customer?.name ?: "ลูกค้าทั่วไป",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "${parked.itemCount} ชิ้น · ${fmtBaht(parked.total)}",
                    style = MaterialTheme.typography.bodyMedium.tabular(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            if (canOverwrite) {
                TextButton(
                    onClick = onOverwrite,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) { Text("ทับ") }
            }
            IconButton(onClick = { confirmingDiscard = true }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "ลบบิลที่พัก",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
    if (confirmingDiscard) {
        AlertDialog(
            onDismissRequest = { confirmingDiscard = false },
            title = { Text("ลบบิลที่พักช่อง $slotNumber?") },
            text = { Text("รายการ ${parked.itemCount} ชิ้นจะถูกลบ ไม่สามารถกู้คืนได้") },
            confirmButton = {
                TextButton(
                    onClick = { confirmingDiscard = false; onDiscard() },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text("ลบ") }
            },
            dismissButton = {
                TextButton(onClick = { confirmingDiscard = false }) { Text("ยกเลิก") }
            },
            shape = MaterialTheme.shapes.large,
        )
    }
}

@Composable
private fun SlotBadge(slotNumber: Int, dimmed: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (dimmed) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp),
    ) {
        androidx.compose.foundation.layout.Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().background(
                if (dimmed) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                text = slotNumber.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (dimmed) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun ParkOverwriteDialog(
    slotNumber: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("ทับบิลที่พักช่อง $slotNumber?") },
        text = { Text("บิลที่พักไว้เดิมจะถูกแทนที่ด้วยบิลปัจจุบัน") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("ทับ") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("ยกเลิก") }
        },
        shape = MaterialTheme.shapes.large,
    )
}

@Composable
fun SwapToParkedDialog(
    slotNumber: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("เปลี่ยนไปใช้บิลที่พักช่อง $slotNumber?") },
        text = { Text("ตะกร้าปัจจุบันจะถูกแทนที่ด้วยบิลที่พัก รายการในตะกร้าเดิมจะหายไป") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("เปลี่ยน") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("ยกเลิก") }
        },
        shape = MaterialTheme.shapes.large,
    )
}

