package app.devper.pharm.presentation.stockcount.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stockcount.CountLotLine
import app.devper.pharm.presentation.stockcount.StockCountFormCallbacks
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText

/**
 * Before saving a count, each lot-tracked drug counted above system stock
 * names the lot its extra units go into (pharmacy-api ADR-0007).
 */
@Composable
internal fun StockCountLotStepModal(
    lines: List<CountLotLine>?,
    callbacks: StockCountFormCallbacks,
) {
    val s = pharmStrings
    PharmModal(
        open = lines != null,
        onDismiss = callbacks.onCancelLotStep,
        size = PharmModalSize.Lg,
        title = s.stockCountLotStepTitle,
        subtitle = s.stockCountLotStepMessage,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = callbacks.onCancelLotStep,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = s.stockCountFormConfirmCta,
                onClick = callbacks.onConfirmLotStep,
                enabled = lines.orEmpty().all { it.target() != null },
                variant = PharmButtonVariant.Primary,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            lines.orEmpty().forEach { line -> LotLine(line, callbacks) }
        }
    }
}

@Composable
private fun LotLine(line: CountLotLine, callbacks: StockCountFormCallbacks) {
    val s = pharmStrings
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "${line.drugName}  +${line.delta}", style = PharmText.body.copy(fontWeight = FontWeight.SemiBold))
        PharmSingleSelectChips(
            chips = line.lots.map { lot ->
                PharmFilterChip(id = lot.id, label = listOfNotNull(lot.lotNumber, lot.expiryDate?.toString()).joinToString(" · "))
            } + PharmFilterChip(id = CountLotLine.NEW_LOT, label = s.stockNewLot),
            activeId = line.choice,
            onSelect = { callbacks.onLotChoice(line.drugId, it) },
        )
        if (line.choice == CountLotLine.NEW_LOT) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PharmTextField(
                    value = line.newLotNumber,
                    onValueChange = { callbacks.onNewLotNumber(line.drugId, it) },
                    placeholder = s.importsFormHeaderLotNumber,
                    modifier = Modifier.weight(1f),
                )
                PharmTextField(
                    value = line.newLotExpiry,
                    onValueChange = { callbacks.onNewLotExpiry(line.drugId, it) },
                    placeholder = "YYYY-MM-DD",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
