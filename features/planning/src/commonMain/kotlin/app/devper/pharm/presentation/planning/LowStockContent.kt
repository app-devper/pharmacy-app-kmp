package app.devper.pharm.presentation.planning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LowStockContent(
    state: LowStockUiState,
    callbacks: LowStockCallbacks = LowStockCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            LowStockHeader(total = state.drugs.size, onReload = callbacks.onReload)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            when {
                state.loading && state.drugs.isEmpty() ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = t.colors.accent)
                    }
                else -> LowStockTable(drugs = state.drugs, callbacks = callbacks)
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun LowStockHeader(total: Int, onReload: () -> Unit) {
    val t = pharmTokens
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).widthIn(min = 200.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = "ยาใกล้หมด", style = PharmText.h1)
            Text(
                text = "ทั้งหมด $total รายการ",
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        }
        PharmButton(
            label = "รีเฟรช",
            onClick = onReload,
            size = PharmButtonSize.Sm,
            variant = PharmButtonVariant.Outline,
            leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
        )
    }
}

private val sampleLowStock = listOf(
    Drug(
        id = "1",
        name = "Paracetamol 500 mg",
        genericName = "พาราเซตามอล",
        type = "tablet",
        strength = "500 mg",
        barcode = null,
        sellPrice = 2.0,
        costPrice = 1.0,
        stock = 0,
        minStock = 50,
        unit = "เม็ด",
        regNo = null,
    ),
    Drug(
        id = "2",
        name = "Amoxicillin 250 mg",
        genericName = "อะม็อกซีซิลลิน",
        type = "capsule",
        strength = "250 mg",
        barcode = null,
        sellPrice = 5.0,
        costPrice = 2.5,
        stock = 12,
        minStock = 40,
        unit = "แคปซูล",
        regNo = null,
    ),
    Drug(
        id = "3",
        name = "Ibuprofen 400 mg",
        genericName = "ไอบูโปรเฟน",
        type = "tablet",
        strength = "400 mg",
        barcode = null,
        sellPrice = 4.0,
        costPrice = 2.0,
        stock = 25,
        minStock = 30,
        unit = "เม็ด",
        regNo = null,
    ),
)

@Preview
@Composable
private fun LowStockContent_Loaded_Preview() {
    PharmacyTheme {
        LowStockContent(state = LowStockUiState(drugs = sampleLowStock))
    }
}

@Preview
@Composable
private fun LowStockContent_Empty_Preview() {
    PharmacyTheme {
        LowStockContent(state = LowStockUiState(drugs = emptyList()))
    }
}

@Preview
@Composable
private fun LowStockContent_Loading_Preview() {
    PharmacyTheme {
        LowStockContent(state = LowStockUiState(loading = true))
    }
}
