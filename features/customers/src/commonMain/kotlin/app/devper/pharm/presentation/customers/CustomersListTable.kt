package app.devper.pharm.presentation.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
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

@Composable
internal fun CustomersListTable(
    customers: List<Customer>,
    callbacks: CustomersListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val s = pharmStrings
    val columns = remember(callbacks, s) {
        listOf(
        PharmTableColumn<Customer>(
            header = s.commonName,
            weight = 1.8f,
            compactTitle = true,
            cell = { customer -> CustomerNameCell(customer) },
        ),
        PharmTableColumn(
            header = s.commonPhone,
            weight = 1.2f,
            cell = { customer -> CustomerPhoneCell(customer) },
        ),
        PharmTableColumn(
            header = s.customersHeaderAllergyShort,
            weight = 1.8f,
            hideInCardWhenEmpty = { customer -> customer.allergyNote.isNullOrBlank() },
            cell = { customer -> CustomerAllergyCell(customer) },
        ),
        PharmTableColumn(
            header = s.customersHeaderTotalSpent,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            hideInCompact = true,
            cell = { CustomerPlaceholderCell() },
        ),
        PharmTableColumn(
            header = s.customersHeaderLastVisit,
            weight = 1.0f,
            hideInCompact = true,
            cell = { CustomerPlaceholderCell() },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { customer -> CustomerRowActions(customer = customer, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = customers,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { callbacks.onOpenDetail(it) },
        rowHeight = 52.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = s.customersListNotFound,
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.Customers,
                    title = s.customersListEmpty,
                )
            }
        },
    )
}

@Composable
private fun CustomerNameCell(customer: Customer) {
    val isVip = customer.priceTier.equals(Tier.Wholesale, ignoreCase = true) ||
        customer.priceTier.equals("vip", ignoreCase = true)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = customer.name,
            style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (isVip) {
            PharmStatusBadge(status = PharmStatus.Vip, size = PharmBadgeSize.Sm)
        }
    }
}

@Composable
private fun CustomerPhoneCell(customer: Customer) {
    val t = pharmTokens
    val phone = customer.phone?.takeIf { it.isNotBlank() }
    if (phone == null) {
        Text(text = "—", style = PharmText.meta.copy(color = t.colors.fgMuted))
    } else {
        Text(
            text = phone,
            style = PharmText.bodySm.copy(color = t.colors.fg2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CustomerAllergyCell(customer: Customer) {
    val t = pharmTokens
    val note = customer.allergyNote?.takeIf { it.isNotBlank() }
    if (note == null) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = PharmIcons.Warning,
            contentDescription = null,
            tint = t.colors.warningFg,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = note,
            style = PharmText.micro.copy(color = t.colors.warningFg),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CustomerPlaceholderCell() {
    val t = pharmTokens
    Text(text = "—", style = PharmText.micro.copy(color = t.colors.fgMuted))
}

@Composable
private fun CustomerRowActions(customer: Customer, callbacks: CustomersListCallbacks) {
    val s = pharmStrings
    val actions = remember(customer.id, callbacks, s) {
        listOf(
            PharmAction(
                label = s.customersActionHistory,
                icon = PharmIcons.SalesHistory,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenDetail(customer) },
            ),
            PharmAction(
                label = s.commonEdit,
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onOpenEdit(customer) },
            ),
            PharmAction(
                label = s.commonDelete,
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onDelete(customer) },
            ),
        )
    }
    PharmActionMenu(actions = actions)
}
