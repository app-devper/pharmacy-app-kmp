package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.presentation.ky.i18n.localizeKy
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Ky9Content(
    state: Ky9UiState,
    callbacks: Ky9Callbacks = Ky9Callbacks(),
) {
    val t = pharmTokens
    val rows = state.entries.map { KyRow.Ky9(it) }
    val totalValue = state.entries.sumOf { it.totalValue }

    PharmListScaffold(
        toolbar = {
            KyToolbar(
                currentForm = KyFormType.Ky9,
                onSwitchForm = callbacks.onSwitchForm,
                month = state.month,
                onMonthChange = callbacks.onMonthChange,
                onApply = callbacks.onApply,
                onExport = callbacks.onExport,
                exporting = state.exporting,
                onAddEntry = callbacks.onAddEntry,
            )
        },
        resultLine = {
            PharmListResultLine(
                total = state.entries.size,
                noun = pharmStrings.kyCountNoun,
                trailing = { KyValueStat(totalValue = totalValue) },
            )
        },
        footer = {
            Text(
                text = pharmStrings.kyToolbarSubtitle,
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        },
    ) {
        when {
            state.loading && state.entries.isEmpty() ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress(color = t.colors.accent)
                }

            else -> KyTable(rows = rows, formType = KyFormType.Ky9)
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeKy(pharmStrings), onDismiss = callbacks.onDismissError)
}

private val sampleKy9Entries = listOf(
    Ky9Entry(
        id = "k1",
        saleId = "s1",
        date = kotlinx.datetime.LocalDate.parse("2026-06-01"),
        drugName = "Tramadol 50mg",
        regNo = "1A 123/45",
        unit = "เม็ด",
        qty = 100,
        pricePerUnit = 2.5,
        totalValue = 250.0,
        seller = "บริษัท ยาดี จำกัด",
        invoiceNo = "INV-2606-001",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-06-01T09:00:00"),
    ),
)

@Preview
@Composable
private fun Ky9Content_Loaded_Preview() {
    PharmacyTheme {
        Ky9Content(
            state = Ky9UiState(month = "2026-06", entries = sampleKy9Entries),
        )
    }
}

@Preview
@Composable
private fun Ky9Content_Loading_Preview() {
    PharmacyTheme {
        Ky9Content(state = Ky9UiState(month = "2026-06", loading = true))
    }
}

@Preview
@Composable
private fun Ky9Content_Empty_Preview() {
    PharmacyTheme {
        Ky9Content(state = Ky9UiState(month = "2026-06", entries = emptyList()))
    }
}
