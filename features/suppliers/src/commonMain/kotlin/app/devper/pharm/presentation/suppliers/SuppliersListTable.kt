package app.devper.pharm.presentation.suppliers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SuppliersListTable(
    suppliers: List<Supplier>,
    callbacks: SuppliersListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val t = pharmTokens
    val s = pharmStrings
    val columns = remember(callbacks, t, s) {
        listOf(
        PharmTableColumn<Supplier>(
            header = s.suppliersHeaderName,
            weight = 2.2f,
            cell = { supplier ->
                Text(
                    text = supplier.name,
                    style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.suppliersHeaderContact,
            weight = 1.4f,
            cell = { supplier ->
                Text(
                    text = supplier.contactName.ifBlank { "—" },
                    style = PharmText.bodySm.copy(
                        color = if (supplier.contactName.isBlank()) t.colors.fgMuted else t.colors.fg2,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.commonPhone,
            weight = 1.2f,
            cell = { supplier ->
                Text(
                    text = supplier.phone.ifBlank { "—" },
                    style = PharmText.meta.copy(
                        color = if (supplier.phone.isBlank()) t.colors.fgMuted else t.colors.fg3,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.suppliersHeaderTaxId,
            weight = 1.3f,
            hideInCompact = true,
            cell = { supplier ->
                Text(
                    text = supplier.taxId.ifBlank { "—" },
                    style = PharmText.micro.copy(
                        color = if (supplier.taxId.isBlank()) t.colors.fgMuted else t.colors.fg3,
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.commonNote,
            weight = 1.6f,
            hideInCompact = true,
            cell = { supplier ->
                Text(
                    text = supplier.notes.ifBlank { "—" },
                    style = PharmText.micro.copy(
                        color = if (supplier.notes.isBlank()) t.colors.fgMuted else t.colors.fg3,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            cell = { supplier -> SupplierRowActions(supplier = supplier, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = suppliers,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { supplier -> callbacks.onOpenDetail(supplier) },
        rowHeight = 56.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = s.suppliersListNotFound,
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.Suppliers,
                    title = s.suppliersListEmpty,
                )
            }
        },
    )
}

@Composable
private fun SupplierRowActions(supplier: Supplier, callbacks: SuppliersListCallbacks) {
    val s = pharmStrings
    val actions = remember(supplier.id, callbacks, s) {
        listOf(
            PharmAction(
                label = s.suppliersHeaderDetails,
                icon = PharmIcons.Suppliers,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenDetail(supplier) },
            ),
            PharmAction(
                label = s.commonEdit,
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onOpenEdit(supplier) },
            ),
            PharmAction(
                label = s.commonDelete,
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onRequestDelete(supplier) },
            ),
        )
    }
    PharmActionMenu(actions = actions)
}
