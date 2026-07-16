package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmBottomSheet
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmIconButton
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun ParkedCartsSheet(
    slots: List<ParkedCart?>,
    canParkActiveCart: Boolean,
    onTapSlot: (Int) -> Unit,
    onDiscardSlot: (Int) -> Unit,
    onRequestOverwrite: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val s = pharmStrings
    PharmBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Text(
                text = s.sellParked,
                style = PharmText.h1,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
            Text(
                text = if (canParkActiveCart) s.sellParkedHintCanPark else s.sellParkedHintEmpty,
                style = PharmText.meta,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
            )

            SheetDivider()

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
                SheetDivider()
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
    val t = pharmTokens
    val s = pharmStrings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pharmClickable(enabled = canPark, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlotBadge(slotNumber, dimmed = !canPark)
        Text(
            text = if (canPark) s.sellParkSlotHere else s.bulkImportEmptyDefault,
            style = PharmText.body.copy(
                fontWeight = FontWeight.Medium,
                color = if (canPark) t.colors.fg1 else t.colors.fgMuted,
            ),
            modifier = Modifier.weight(1f).padding(start = 12.dp),
        )
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
    val t = pharmTokens
    val s = pharmStrings
    var confirmingDiscard by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pharmClickable(role = Role.Button, onClick = onRestore)
            .padding(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlotBadge(slotNumber)
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                text = parked.customer?.name ?: s.sellCustomerWalkIn,
                style = PharmText.body.copy(fontWeight = FontWeight.SemiBold, color = t.colors.fg1),
            )
            Text(
                text = s.sellParkedSummary(parked.itemCount, fmtBaht(parked.total.amount)),
                style = PharmText.meta.tabular(),
            )
        }
        if (canOverwrite) {
            PharmButton(
                label = s.commonConfirm,
                onClick = onOverwrite,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
        }
        PharmIconButton(
            contentDescription = s.sellParkedDeleteDesc,
            onClick = { confirmingDiscard = true },
        ) {
            Icon(
                imageVector = PharmIcons.Trash,
                contentDescription = null,
                tint = t.colors.dangerFg,
                modifier = Modifier.size(18.dp),
            )
        }
    }

    PharmModal(
        open = confirmingDiscard,
        onDismiss = { confirmingDiscard = false },
        title = s.sellParkedDeleteTitle(slotNumber),
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = { confirmingDiscard = false },
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = s.commonDelete,
                onClick = {
                    confirmingDiscard = false
                    onDiscard()
                },
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(s.sellParkedDeleteBody(parked.itemCount), style = PharmText.body)
    }
}

@Composable
private fun SlotBadge(slotNumber: Int, dimmed: Boolean = false) {
    val t = pharmTokens
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (dimmed) t.colors.borderSubtle else t.colors.accent),
    ) {
        Text(
            text = slotNumber.toString(),
            style = PharmText.badge.copy(
                fontWeight = FontWeight.Bold,
                color = if (dimmed) t.colors.fg2 else t.colors.surface,
            ),
        )
    }
}

@Composable
private fun SheetDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(pharmTokens.colors.divider),
    )
}

@Composable
fun ParkOverwriteDialog(
    slotNumber: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val s = pharmStrings
    PharmModal(
        open = true,
        onDismiss = onCancel,
        title = s.sellParkedOverwriteTitle(slotNumber),
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onCancel,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = s.commonConfirm,
                onClick = onConfirm,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(s.sellParkedOverwriteBody, style = PharmText.body)
    }
}
