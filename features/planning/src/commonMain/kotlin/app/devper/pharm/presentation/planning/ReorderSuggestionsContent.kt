package app.devper.pharm.presentation.planning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmSubPage
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReorderSuggestionsContent(
    state: ReorderSuggestionsUiState,
    onBack: () -> Unit,
    callbacks: ReorderSuggestionsCallbacks = ReorderSuggestionsCallbacks(),
) {
    val t = pharmTokens

    PharmSubPage(
        title = "คำแนะนำสั่งซื้อ",
        subtitle = "รายการที่แนะนำให้สั่งซื้อเพิ่ม",
        onBack = onBack,
        actions = {
            PharmButton(
                label = "รีเฟรช",
                onClick = callbacks.onReload,
                size = PharmButtonSize.Sm,
                variant = PharmButtonVariant.Outline,
                leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            PharmListResultLine(total = state.suggestions.size, noun = "รายการ")
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            when {
                state.loading && state.suggestions.isEmpty() -> PharmListSkeleton()
                state.suggestions.isEmpty() ->
                    PharmEmptyState(
                        icon = PharmIcons.Reports,
                        title = "ไม่มีรายการที่ต้องสั่งซื้อ",
                        subtitle = "ยังไม่มียาที่ถึงเกณฑ์แนะนำให้สั่งซื้อเพิ่ม",
                    )
                else -> ReorderSuggestionsTable(suggestions = state.suggestions, callbacks = callbacks)
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

private val sampleSuggestions = listOf(
    ReorderSuggestion(
        drugId = "1",
        drugName = "Paracetamol 500 mg",
        unit = "เม็ด",
        currentStock = 0,
        minStock = 50,
        qtySold = 320,
        avgDailySale = 10.6,
        daysLeft = 0.0,
        suggestedQty = 200,
        costPrice = 1.20,
        sellPrice = 2.0,
    ),
    ReorderSuggestion(
        drugId = "2",
        drugName = "Amoxicillin 250 mg",
        unit = "แคปซูล",
        currentStock = 18,
        minStock = 40,
        qtySold = 95,
        avgDailySale = 3.2,
        daysLeft = 5.6,
        suggestedQty = 100,
        costPrice = 2.50,
        sellPrice = 5.0,
    ),
    ReorderSuggestion(
        drugId = "3",
        drugName = "Cetirizine 10 mg",
        unit = "เม็ด",
        currentStock = 25,
        minStock = 30,
        qtySold = 60,
        avgDailySale = 2.0,
        daysLeft = 12.5,
        suggestedQty = 60,
        costPrice = 0.80,
        sellPrice = 1.5,
    ),
)

@Preview
@Composable
private fun ReorderSuggestionsContent_Loaded_Preview() {
    PharmacyTheme {
        ReorderSuggestionsContent(state = ReorderSuggestionsUiState(suggestions = sampleSuggestions), onBack = {})
    }
}

@Preview
@Composable
private fun ReorderSuggestionsContent_Empty_Preview() {
    PharmacyTheme {
        ReorderSuggestionsContent(state = ReorderSuggestionsUiState(suggestions = emptyList()), onBack = {})
    }
}

@Preview
@Composable
private fun ReorderSuggestionsContent_Loading_Preview() {
    PharmacyTheme {
        ReorderSuggestionsContent(state = ReorderSuggestionsUiState(loading = true), onBack = {})
    }
}
