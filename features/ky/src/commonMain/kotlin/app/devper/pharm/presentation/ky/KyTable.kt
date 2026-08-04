package app.devper.pharm.presentation.ky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStickyTotalRow
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun KyTable(
    rows: List<KyRow>,
    formType: KyFormType,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val display = remember(rows) { rows.mapIndexed { index, row -> kyRowDisplay(row, index + 1) } }
    val totalQty = remember(rows) { display.sumOf { it.qty } }
    val totalValue = remember(rows) { display.sumOf { it.totalValue ?: 0.0 } }
    val partyHeader = if (formType == KyFormType.Ky9) s.kySupplierBusinessNo else s.kyBuyerPatient
    val refHeader = if (formType == KyFormType.Ky9) s.kyDeliveryNoteNo else s.kyHeaderRef
    val columns = kyColumns(s = s, partyHeader = partyHeader, refHeader = refHeader)

    PharmTable(
        rows = display,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        emptyContent = {
            PharmEmptyState(
                icon = PharmIcons.KyForms,
                title = s.kyEmptyMonth,
            )
        },
        bottomRow = {
            PharmStickyTotalRow(
                label = s.kyTotalAll,
                totalText = fmtBaht(totalValue),
                subtotalText = "$totalQty ${s.commonUnit}",
            )
        },
    )
}

private fun kyColumns(
    s: app.devper.pharm.ui.i18n.PharmStrings,
    partyHeader: String,
    refHeader: String,
): List<PharmTableColumn<KyRowDisplay>> = listOf(
    PharmTableColumn(
        header = "#",
        weight = 0.4f,
        cell = { row -> IndexCell(row.index) },
    ),
    PharmTableColumn(
        header = s.kyHeaderDate,
        weight = 1.0f,
        cell = { row -> DateCell(row.date) },
    ),
    PharmTableColumn(
        header = s.kyHeaderItem,
        compactTitle = true,
        weight = 2.2f,
        cell = { row -> DrugNameCell(row.drugName) },
    ),
    PharmTableColumn(
        header = s.kyDrugRegistration,
        weight = 1.2f,
        cell = { row -> MonoCell(row.regNo) },
    ),
    PharmTableColumn(
        header = s.commonUnit,
        weight = 0.7f,
        cell = { row -> MetaCell(row.unit) },
    ),
    PharmTableColumn(
        header = s.kyHeaderQty,
        weight = 0.7f,
        align = PharmColumnAlign.End,
        cell = { row -> NumCell(row.qty.toString()) },
    ),
    PharmTableColumn(
        header = s.kyHeaderPricePerUnit,
        weight = 1.0f,
        align = PharmColumnAlign.End,
        cell = { row -> NumCell(row.pricePerUnit?.let { fmtBaht(it) } ?: "—") },
    ),
    PharmTableColumn(
        header = s.kyValueAmount,
        compactTrailing = true,
        weight = 1.1f,
        align = PharmColumnAlign.End,
        cell = { row -> TotalCell(row.totalValue) },
    ),
    PharmTableColumn(
        header = partyHeader,
        weight = 1.6f,
        cell = { row -> PartyCell(row.party) },
    ),
    PharmTableColumn(
        header = refHeader,
        weight = 1.4f,
        cell = { row -> MetaCell(row.reference) },
    ),
)

@Composable
private fun IndexCell(index: Int) {
    val t = pharmTokens
    Text(
        text = index.toString(),
        style = PharmText.micro.copy(
            color = t.colors.fgMuted,
            fontFeatureSettings = "tnum",
        ),
    )
}

@Composable
private fun DateCell(date: kotlinx.datetime.LocalDate?) {
    val t = pharmTokens
    Text(
        text = app.devper.pharm.ui.format.localDateToBuddhist(date),
        style = PharmText.bodySm.copy(
            color = t.colors.fg2,
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun DrugNameCell(name: String) {
    val t = pharmTokens
    Text(
        text = name,
        style = PharmText.bodySm.copy(
            color = t.colors.fg1,
            fontWeight = FontWeight.Medium,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun MonoCell(text: String) {
    val t = pharmTokens
    Text(
        text = text.ifBlank { "—" },
        style = PharmText.micro.copy(
            color = t.colors.fg3,
            fontFamily = FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun MetaCell(text: String) {
    val t = pharmTokens
    Text(
        text = text.ifBlank { "—" },
        style = PharmText.meta.copy(color = t.colors.fg3),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun NumCell(text: String) {
    val t = pharmTokens
    Text(
        text = text,
        style = PharmText.bodySm.copy(
            color = t.colors.fg2,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun TotalCell(value: Double?) {
    val t = pharmTokens
    Text(
        text = value?.let { fmtBaht(it) } ?: "—",
        style = PharmText.bodySm.copy(
            color = t.colors.fg1,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun PartyCell(text: String) {
    val t = pharmTokens
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.ifBlank { "—" },
            style = PharmText.bodySm.copy(color = t.colors.fg2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal data class KyRowDisplay(
    val id: String,
    val index: Int,
    val date: kotlinx.datetime.LocalDate?,
    val drugName: String,
    val regNo: String,
    val unit: String,
    val qty: Int,
    val pricePerUnit: Double?,
    val totalValue: Double?,
    val party: String,
    val reference: String,
)

internal fun kyRowDisplay(row: KyRow, index: Int): KyRowDisplay = when (row) {
    is KyRow.Ky9 -> KyRowDisplay(
        id = row.id,
        index = index,
        date = row.entry.date,
        drugName = row.entry.drugName,
        regNo = row.entry.regNo,
        unit = row.entry.unit,
        qty = row.entry.qty,
        pricePerUnit = row.entry.pricePerUnit,
        totalValue = row.entry.totalValue,
        party = row.entry.seller,
        reference = row.entry.invoiceNo,
    )

    is KyRow.Ky10 -> KyRowDisplay(
        id = row.id,
        index = index,
        date = row.entry.date,
        drugName = row.entry.drugName,
        regNo = row.entry.regNo,
        unit = row.entry.unit,
        qty = row.entry.qty,
        pricePerUnit = null,
        totalValue = null,
        party = row.entry.buyerName,
        reference = listOfNotNull(
            row.entry.rxNo.takeIf { it.isNotBlank() }?.let { "Rx $it" },
            row.entry.doctor.takeIf { it.isNotBlank() }?.let { it },
        ).joinToString(" · "),
    )

    is KyRow.Ky11 -> KyRowDisplay(
        id = row.id,
        index = index,
        date = row.entry.date,
        drugName = row.entry.drugName,
        regNo = row.entry.regNo,
        unit = row.entry.unit,
        qty = row.entry.qty,
        pricePerUnit = null,
        totalValue = null,
        party = row.entry.buyerName,
        reference = row.entry.purpose,
    )

    is KyRow.Ky12 -> KyRowDisplay(
        id = row.id,
        index = index,
        date = row.entry.date,
        drugName = row.entry.drugName,
        regNo = "",
        unit = row.entry.unit,
        qty = row.entry.qty,
        pricePerUnit = if (row.entry.qty > 0) row.entry.totalValue / row.entry.qty else null,
        totalValue = row.entry.totalValue,
        party = row.entry.patientName,
        reference = listOfNotNull(
            row.entry.rxNo.takeIf { it.isNotBlank() }?.let { "Rx $it" },
            row.entry.hospital.takeIf { it.isNotBlank() },
        ).joinToString(" · "),
    )
}
