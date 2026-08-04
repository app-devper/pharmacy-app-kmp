package app.devper.pharm.presentation.bulkimport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun BulkImportResultTable(
    rows: List<BulkImportRow>,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val s = pharmStrings

    val columns = remember(t, s) {
        listOf(
        PharmTableColumn<BulkImportRow>(
            header = "#",
            weight = 0.5f,
            align = PharmColumnAlign.End,
            cell = { row ->
                Text(
                    text = row.row.toString(),
                    style = PharmText.bodySm.copy(
                        color = t.colors.fg3,
                        fontFamily = FontFamily.Monospace,
                    ).tabular(),
                )
            },
        ),
        PharmTableColumn(
            header = s.expiryHeaderDrugName,
            compactTitle = true,
            weight = 3f,
            cell = { row ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = row.name.ifBlank { pharmStrings.commonUnnamed },
                        style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    row.errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            style = PharmText.micro.copy(color = t.colors.dangerFg),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
        ),
        PharmTableColumn(
            header = s.commonQty,
            weight = 0.9f,
            align = PharmColumnAlign.End,
            cell = { row ->
                Text(
                    text = "${row.qty} ${row.unit}",
                    style = PharmText.bodySm.copy(
                        color = t.colors.fg2,
                        fontFeatureSettings = "tnum",
                    ),
                )
            },
        ),
        PharmTableColumn(
            header = s.commonStatus,
            weight = 1.1f,
            cell = { row -> BulkImportStatusBadge(row.status) },
        ),
        )
    }

    Box(modifier = modifier.fillMaxWidth()) {
        PharmTable(
            rows = rows,
            columns = columns,
            key = { it.row },
            emptyContent = {
                PharmEmptyState(
                    icon = PharmIcons.Imports,
                    title = s.bulkImportEmptyDefault,
                    subtitle = s.bulkImportValidatePromptHint,
                )
            },
        )
    }
}

@Composable
private fun BulkImportStatusBadge(status: BulkImportRowStatus) {
    when (status) {
        BulkImportRowStatus.Done    -> PharmStatusBadge(PharmStatus.Done, size = PharmBadgeSize.Sm)
        BulkImportRowStatus.Failed  -> PharmStatusBadge(PharmStatus.Failed, size = PharmBadgeSize.Sm)
        BulkImportRowStatus.Pending -> PharmStatusBadge(PharmStatus.Pending, label = pharmStrings.bulkImportReadyBadge, size = PharmBadgeSize.Sm)
    }
}
