package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.extension.resolvePrice
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.i18n.pharmStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AltUnitPickerSheet(
    drug: Drug,
    activeTier: String,
    onPick: (AltUnit?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val altUnits = drug.altUnits.filterNot { it.hidden }
    val t = pharmTokens

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Text(
                text = drug.name,
                style = PharmText.h2,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            Text(
                text = pharmStrings.sellPickUnit,
                style = PharmText.meta,
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp),
            )

            UnitRow(
                label = drug.unit ?: pharmStrings.commonUnit,
                factorHint = null,
                price = resolvePrice(drug.sellPrice, drug.prices, activeTier).amount,
                onClick = { onPick(null) },
            )
            Divider()
            altUnits.forEach { alt ->
                UnitRow(
                    label = alt.name,
                    factorHint = "× ${alt.factor} ${drug.unit ?: ""}".trim(),
                    price = resolvePrice(alt.sellPrice, alt.prices, activeTier).amount,
                    onClick = { onPick(alt) },
                )
                Divider()
            }
        }
    }
}

@Composable
private fun UnitRow(label: String, factorHint: String?, price: Double, onClick: () -> Unit) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pharmClickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = label,
                style = PharmText.body.copy(fontWeight = FontWeight.Medium),
            )
            factorHint?.let {
                Text(text = it, style = PharmText.micro)
            }
        }
        Text(text = fmtBaht(price), style = PharmText.price)
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(pharmTokens.colors.borderSubtle),
    )
}
