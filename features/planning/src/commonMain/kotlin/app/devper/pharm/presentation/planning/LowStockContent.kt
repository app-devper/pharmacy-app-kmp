package app.devper.pharm.presentation.planning

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LowStockContent(
    state: LowStockUiState,
    callbacks: LowStockCallbacks = LowStockCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        LowStockToolbar(onReload = callbacks.onReload)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            PharmListResultLine(total = state.drugs.size, noun = "รายการ")
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            when {
                state.loading && state.drugs.isEmpty() -> PharmListSkeleton()
                state.drugs.isEmpty() ->
                    PharmEmptyState(
                        icon = PharmIcons.Stock,
                        title = "ไม่มียาใกล้หมด",
                        subtitle = "สต็อกยาทุกรายการสูงกว่าระดับขั้นต่ำ",
                    )
                else -> LowStockTable(drugs = state.drugs, callbacks = callbacks)
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun LowStockToolbar(onReload: () -> Unit) {
    PharmListToolbar(
        title = "ยาใกล้หมด",
        subtitle = "ยาที่ต่ำกว่าระดับสต็อกขั้นต่ำ",
        actions = {
            PharmButton(
                label = "รีเฟรช",
                onClick = onReload,
                size = PharmButtonSize.Sm,
                variant = PharmButtonVariant.Outline,
                leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
            )
        },
    )
}

private val sampleLowStock = listOf(
    Drug(
        id = "1",
        name = "Paracetamol 500 mg",
        genericName = "พาราเซตามอล",
        type = "tablet",
        strength = "500 mg",
        barcode = null,
        sellPrice = Money(2.0),
        costPrice = Money(1.0),
        stock = Quantity(0),
        minStock = Quantity(50),
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
        sellPrice = Money(5.0),
        costPrice = Money(2.5),
        stock = Quantity(12),
        minStock = Quantity(40),
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
        sellPrice = Money(4.0),
        costPrice = Money(2.0),
        stock = Quantity(25),
        minStock = Quantity(30),
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
