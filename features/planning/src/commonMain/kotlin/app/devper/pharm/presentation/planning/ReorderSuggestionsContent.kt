package app.devper.pharm.presentation.planning

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.presentation.planning.i18n.localizePlanning
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.components.SubPageBar
import app.devper.pharm.ui.i18n.pharmStrings
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
    val s = pharmStrings

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(
            title = s.planningTitle,
            onBack = onBack,
            actions = {
                PharmButton(
                    label = s.planningRefreshCta,
                    onClick = callbacks.onReload,
                    size = PharmButtonSize.Sm,
                    variant = PharmButtonVariant.Outline,
                    leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            PharmListResultLine(total = state.suggestions.size, noun = s.planningCountNoun)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            when {
                state.loading && state.suggestions.isEmpty() -> PharmListSkeleton()
                state.suggestions.isEmpty() ->
                    PharmEmptyState(
                        icon = PharmIcons.Reports,
                        title = s.planningReorderEmptyTitle,
                        subtitle = s.planningReorderEmpty,
                    )
                else -> ReorderSuggestionsTable(suggestions = state.suggestions, callbacks = callbacks)
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizePlanning(pharmStrings), onDismiss = callbacks.onDismissError)
}

private val sampleSuggestions = listOf(
    ReorderSuggestion(
        drugId = "1",
        drugName = "Paracetamol 500 mg",
        unit = "เม็ด",
        currentStock = Quantity(0),
        minStock = Quantity(50),
        qtySold = Quantity(320),
        avgDailySale = 10.6,
        daysLeft = 0.0,
        suggestedQty = Quantity(200),
        costPrice = Money(1.20),
        sellPrice = Money(2.0),
    ),
    ReorderSuggestion(
        drugId = "2",
        drugName = "Amoxicillin 250 mg",
        unit = "แคปซูล",
        currentStock = Quantity(18),
        minStock = Quantity(40),
        qtySold = Quantity(95),
        avgDailySale = 3.2,
        daysLeft = 5.6,
        suggestedQty = Quantity(100),
        costPrice = Money(2.50),
        sellPrice = Money(5.0),
    ),
    ReorderSuggestion(
        drugId = "3",
        drugName = "Cetirizine 10 mg",
        unit = "เม็ด",
        currentStock = Quantity(25),
        minStock = Quantity(30),
        qtySold = Quantity(60),
        avgDailySale = 2.0,
        daysLeft = 12.5,
        suggestedQty = Quantity(60),
        costPrice = Money(0.80),
        sellPrice = Money(1.5),
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
