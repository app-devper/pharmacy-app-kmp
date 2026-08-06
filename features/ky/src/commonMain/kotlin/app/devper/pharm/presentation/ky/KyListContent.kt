package app.devper.pharm.presentation.ky

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.presentation.ky.i18n.localizeKy
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun KyListContent(
    state: KyListUiState,
    callbacks: KyListCallbacks = KyListCallbacks(),
) {
    val t = pharmTokens
    PharmListScaffold(
        toolbar = {
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
        },
        resultLine = {
            PharmListResultLine(
                total = state.rows.size,
                noun = pharmStrings.kyCountNoun,
                trailing = {
                    KyValueStat(totalValue = state.rows.sumOf { row -> rowTotalValue(row) })
                },
            )
        },
    ) {
        when {
            state.loading && state.rows.isEmpty() -> PharmListSkeleton()
            state.errorState != null && state.rows.isEmpty() ->
                PharmErrorState()
            state.rows.isEmpty() -> PharmEmptyState(
                icon = PharmIcons.KyForms,
                title = pharmStrings.kyEmptyMonth,
            )
            else -> KyTable(rows = state.rows, formType = state.formType)
        }
    }

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(state.rows.isEmpty())?.localizeKy(pharmStrings), onDismiss = callbacks.onDismissError)
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
                messageState = app.devper.pharm.common.error.CommonUiStateMessage.ExportDone("ส่งออก PDF สำเร็จ"),
            ),
        )
    }
}
