package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.i18n.pharmStrings

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
    val t = pharmTokens
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = pharmStrings.sellParked,
                style = PharmText.h1,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Text(
                text = if (canParkActiveCart) {
                    pharmStrings.sellParkedHintCanPark
                } else {
                    pharmStrings.sellParkedHintEmpty
                },
                style = PharmText.meta,
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
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.bgPage)
            .clickable(enabled = canPark, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SlotBadge(slotNumber, dimmed = !canPark)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (canPark) pharmStrings.sellParkSlotHere else pharmStrings.bulkImportEmptyDefault,
                style = PharmText.body.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (canPark) t.colors.fg1 else t.colors.fgMuted,
                ),
            )
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
    val t = pharmTokens
    var confirmingDiscard by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.accentBgSoft)
            .clickable(role = Role.Button, onClick = onRestore)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SlotBadge(slotNumber)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = parked.customer?.name ?: pharmStrings.sellCustomerWalkIn,
                style = PharmText.body.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = t.colors.fg1,
                ),
            )
            Text(
                text = pharmStrings.sellParkedSummary(parked.itemCount, fmtBaht(parked.total.amount)),
                style = PharmText.meta.tabular(),
            )
        }

        if (canOverwrite) {
            PharmButton(
                label = pharmStrings.commonConfirm,
                onClick = onOverwrite,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
        }
        IconButton(onClick = { confirmingDiscard = true }) {
            Icon(
                imageVector = PharmIcons.Close,
                contentDescription = pharmStrings.sellParkedDeleteDesc,
                tint = t.colors.fg2,
                modifier = Modifier.size(20.dp),
            )
        }
    }

    PharmModal(
        open = confirmingDiscard,
        onDismiss = { confirmingDiscard = false },
        title = pharmStrings.sellParkedDeleteTitle(slotNumber),
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonCancel,
                onClick = { confirmingDiscard = false },
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = pharmStrings.commonDelete,
                onClick = {
                    confirmingDiscard = false
                    onDiscard()
                },
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(
            pharmStrings.sellParkedDeleteBody(parked.itemCount),
            style = PharmText.body,
        )
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
fun ParkOverwriteDialog(
    slotNumber: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onCancel,
        title = pharmStrings.sellParkedOverwriteTitle(slotNumber),
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonCancel,
                onClick = onCancel,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = pharmStrings.commonConfirm,
                onClick = onConfirm,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(pharmStrings.sellParkedOverwriteBody, style = PharmText.body)
    }
}

@Composable
fun SwapToParkedDialog(
    slotNumber: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onCancel,
        title = pharmStrings.sellParkedSwapTitle(slotNumber),
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonCancel,
                onClick = onCancel,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = pharmStrings.commonEdit,
                onClick = onConfirm,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(
            pharmStrings.sellParkedSwapBody,
            style = PharmText.body,
        )
    }
}
