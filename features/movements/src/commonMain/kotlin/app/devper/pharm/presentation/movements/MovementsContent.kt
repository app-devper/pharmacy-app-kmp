package app.devper.pharm.presentation.movements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.presentation.movements.i18n.localizeMovements
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton

@Composable
fun MovementsContent(
    state: MovementsUiState,
    callbacks: MovementsCallbacks = MovementsCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        MovementsListToolbar(state = state, callbacks = callbacks)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            PharmListResultLine(total = state.items.size, noun = pharmStrings.movementsCountNoun)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.items.isEmpty() ->
                    PharmListSkeleton(modifier = Modifier.fillMaxSize())
                else -> MovementsTable(state = state, callbacks = callbacks)
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeMovements(pharmStrings), onDismiss = callbacks.onDismissError)
}

private val sampleMovements = listOf(
    StockMovement(
        id = "1", type = MovementType.Sale,
        drugId = "d1", drugName = "พาราเซตามอล 500mg",
        delta = -10, reference = "SC-260516-014", note = "by: ภ. ปรียา",
        at = "2026-05-17T14:42:00",
    ),
    StockMovement(
        id = "2", type = MovementType.Sale,
        drugId = "d2", drugName = "อะม็อกซีซิลลิน 500mg",
        delta = -21, reference = "SC-260516-014", note = "by: ภ. ปรียา",
        at = "2026-05-17T14:42:00",
    ),
    StockMovement(
        id = "3", type = MovementType.Import,
        drugId = "d3", drugName = "วิตามินซี 1000mg",
        delta = 24, reference = "IMP-260516-002", note = "by: ภ. ปรียา",
        at = "2026-05-17T13:30:00",
    ),
    StockMovement(
        id = "4", type = MovementType.Adjustment,
        drugId = "d4", drugName = "ฟ้าทะลายโจร แคปซูล",
        delta = -2, reference = "SC-260516-001", note = "by: ภ. ปรียา",
        at = "2026-05-17T12:15:00",
    ),
    StockMovement(
        id = "5", type = MovementType.Return,
        drugId = "d5", drugName = "ลอราทาดีน 10mg",
        delta = 4, reference = "RET-260516-001", note = "by: น. สุมาลี",
        at = "2026-05-17T11:50:00",
    ),
    StockMovement(
        id = "6", type = MovementType.Sale,
        drugId = "d6", drugName = "ออเมพราโซล 20mg",
        delta = -14, reference = "SC-260516-006", note = "by: ภ. ปรียา",
        at = "2026-05-17T11:42:00",
    ),
    StockMovement(
        id = "7", type = MovementType.Writeoff,
        drugId = "d7", drugName = "ไอบูโพรเฟน 400mg",
        delta = -8, reference = "EXP-260516-001", note = "หมดอายุ · by: ภ. ปรียา",
        at = "2026-05-16T17:45:00",
    ),
)

@Preview
@Composable
private fun MovementsContent_Loaded_Preview() {
    PharmacyTheme {
        MovementsContent(
            state = MovementsUiState(
                items = sampleMovements,
                total = sampleMovements.size,
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-04-17", to = "2026-05-17"),
            ),
        )
    }
}

@Preview
@Composable
private fun MovementsContent_Empty_Preview() {
    PharmacyTheme {
        MovementsContent(
            state = MovementsUiState(
                items = emptyList(),
                total = 0,
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-04-17", to = "2026-05-17"),
            ),
        )
    }
}

@Preview
@Composable
private fun MovementsContent_Loading_Preview() {
    PharmacyTheme {
        MovementsContent(state = MovementsUiState(loading = true))
    }
}
