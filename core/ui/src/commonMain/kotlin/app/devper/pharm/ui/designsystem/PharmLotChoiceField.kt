package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LotChoice
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun PharmLotChoiceField(
    lot: LotChoice,
    onChoose: (String) -> Unit,
    onNewLotNumber: (String) -> Unit,
    onNewLotExpiry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmSingleSelectChips(
            chips = lot.lots.map { l ->
                PharmFilterChip(id = l.id, label = listOfNotNull(l.lotNumber, l.expiryDate?.toString()).joinToString(" · "))
            } + PharmFilterChip(id = LotChoice.NEW_LOT, label = s.stockNewLot),
            activeId = lot.choice,
            onSelect = onChoose,
        )
        if (lot.choosingNewLot) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PharmTextField(
                    value = lot.newLotNumber,
                    onValueChange = onNewLotNumber,
                    placeholder = s.importsFormHeaderLotNumber,
                    modifier = Modifier.weight(1f),
                )
                PharmTextField(
                    value = lot.newLotExpiry,
                    onValueChange = onNewLotExpiry,
                    placeholder = "YYYY-MM-DD",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
