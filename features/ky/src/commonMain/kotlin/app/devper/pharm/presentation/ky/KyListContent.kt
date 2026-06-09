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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.presentation.ky.i18n.localizeKy
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KyListContent(
    state: KyListUiState,
    callbacks: KyListCallbacks = KyListCallbacks(),
) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        KyToolbar(
            currentForm = state.formType,
            onSwitchForm = callbacks.onSwitchForm,
            month = state.month,
            onMonthChange = callbacks.onMonthChange,
            onApply = callbacks.onApply,
            onExport = callbacks.onExport,
            exporting = state.exporting,
            onAddEntry = callbacks.onAddEntry,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            state.message?.let { msg -> KyMessageBanner(msg, callbacks.onDismissMessage) }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(t.shapes.lg)
                    .background(t.colors.surface)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
            ) {
                PharmListResultLine(
                    total = state.rows.size,
                    noun = pharmStrings.kyCountNoun,
                    trailing = {
                        KyValueStat(totalValue = state.rows.sumOf { row -> rowTotalValue(row) })
                    },
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

                when {
                    state.loading && state.rows.isEmpty() -> PharmListSkeleton()

                    else -> KyTable(rows = state.rows, formType = state.formType)
                }
            }
            Text(
                text = pharmStrings.kyToolbarSubtitle,
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeKy(pharmStrings), onDismiss = callbacks.onDismissError)
}

private fun rowTotalValue(row: KyRow): Double = when (row) {
    is KyRow.Ky9  -> row.entry.totalValue
    is KyRow.Ky10 -> 0.0
    is KyRow.Ky11 -> 0.0
    is KyRow.Ky12 -> row.entry.totalValue
}

private val sampleKy10Rows = listOf(
    KyRow.Ky10(
        Ky10Entry(
            id = "e1",
            saleId = "",
            date = kotlinx.datetime.LocalDate(2026, 5, 2),
            drugName = "ออเมพราโซล 20mg",
            regNo = "1A 311/55",
            qty = 14,
            unit = "แคปซูล",
            buyerName = "นาง สุดา สมใจ",
            buyerAddress = "กทม.",
            rxNo = "240501",
            doctor = "X",
            balance = 80,
            createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-02T10:00:00"),
        ),
    ),
    KyRow.Ky10(
        Ky10Entry(
            id = "e2",
            saleId = "",
            date = kotlinx.datetime.LocalDate(2026, 5, 6),
            drugName = "ยาแก้ปวด ทรามาดอล 50mg",
            regNo = "1A 200/58",
            qty = 10,
            unit = "แคปซูล",
            buyerName = "นาย วรพล สุขสันต์",
            buyerAddress = "กทม.",
            rxNo = "240515",
            doctor = "Y",
            balance = 90,
            createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-06T11:00:00"),
        ),
    ),
)

private val sampleKy11Rows = listOf(
    KyRow.Ky11(
        Ky11Entry(
            id = "e3",
            saleId = "",
            date = kotlinx.datetime.LocalDate(2026, 5, 1),
            drugName = "อะม็อกซีซิลลิน 500mg",
            regNo = "1A 091/52",
            qty = 21,
            unit = "แคปซูล",
            buyerName = "นาง สุดา สมใจ",
            purpose = "—",
            pharmacist = "ภญ.A",
            createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-01T09:00:00"),
        ),
    ),
    KyRow.Ky11(
        Ky11Entry(
            id = "e4",
            saleId = "",
            date = kotlinx.datetime.LocalDate(2026, 5, 4),
            drugName = "ลอราทาดีน 10mg",
            regNo = "1A 044/58",
            qty = 10,
            unit = "เม็ด",
            buyerName = "นาย ธีรพงษ์ ใจเย็น",
            purpose = "—",
            pharmacist = "ภญ.A",
            createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-04T10:30:00"),
        ),
    ),
)

private val sampleKy12Rows = listOf(
    KyRow.Ky12(
        Ky12Entry(
            id = "e5",
            saleId = "",
            date = kotlinx.datetime.LocalDate(2026, 5, 7),
            rxNo = "240601",
            patientName = "นาย เอกชัย สุภาพ",
            doctor = "นพ.Z",
            hospital = "รพ.ABC",
            drugName = "ซาลบูทามอล MDI",
            qty = 1,
            unit = "หลอด",
            totalValue = 95.0,
            status = "issued",
            createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-07T14:20:00"),
        ),
    ),
)

@Preview
@Composable
private fun KyListContent_Ky10_Loaded_Preview() {
    PharmacyTheme {
        KyListContent(
            state = KyListUiState(
                formType = KyFormType.Ky10,
                month = "2026-05",
                rows = sampleKy10Rows,
            ),
        )
    }
}

@Preview
@Composable
private fun KyListContent_Ky11_Loaded_Preview() {
    PharmacyTheme {
        KyListContent(
            state = KyListUiState(
                formType = KyFormType.Ky11,
                month = "2026-05",
                rows = sampleKy11Rows,
            ),
        )
    }
}

@Preview
@Composable
private fun KyListContent_Ky12_LoadedWithTotal_Preview() {
    PharmacyTheme {
        KyListContent(
            state = KyListUiState(
                formType = KyFormType.Ky12,
                month = "2026-05",
                rows = sampleKy12Rows,
            ),
        )
    }
}

@Preview
@Composable
private fun KyListContent_Empty_Preview() {
    PharmacyTheme {
        KyListContent(state = KyListUiState(formType = KyFormType.Ky10, month = "2026-05"))
    }
}

@Preview
@Composable
private fun KyListContent_Loading_Preview() {
    PharmacyTheme {
        KyListContent(state = KyListUiState(formType = KyFormType.Ky12, loading = true))
    }
}

@Preview
@Composable
private fun KyListContent_WithMessage_Preview() {
    PharmacyTheme {
        KyListContent(
            state = KyListUiState(
                formType = KyFormType.Ky11,
                month = "2026-05",
                rows = sampleKy11Rows,
                message = "ส่งออก PDF สำเร็จ",
            ),
        )
    }
}
