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
import app.devper.pharm.domain.pricing.Tier
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun CustomersListTable(
    customers: List<Customer>,
    callbacks: CustomersListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val columns = remember(callbacks) {
        listOf(
        PharmTableColumn<Customer>(
            header = "ชื่อ",
            weight = 1.8f,
            cell = { customer -> CustomerNameCell(customer) },
        ),
        PharmTableColumn(
            header = "เบอร์โทร",
            weight = 1.2f,
            cell = { customer -> CustomerPhoneCell(customer) },
        ),
        PharmTableColumn(
            header = "โรคประจำตัว / แพ้ยา",
            weight = 1.8f,
            cell = { customer -> CustomerAllergyCell(customer) },
        ),
        PharmTableColumn(
            header = "ยอดซื้อรวม",
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { CustomerPlaceholderCell() },
        ),
        PharmTableColumn(
            header = "มาล่าสุด",
            weight = 1.0f,
            cell = { CustomerPlaceholderCell() },
        ),
        PharmTableColumn(
            header = "จัดการ",
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
        rowHeight = 56.dp,
        emptyContent = {
            Text(
                text = if (emptySearching) "ไม่พบลูกค้าตามที่ค้นหา" else "ยังไม่มีรายชื่อลูกค้า",
                style = PharmText.meta,
            )
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
    if (note == null) {
        Text(text = "—", style = PharmText.micro.copy(color = t.colors.fgMuted))
        return
    }
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
    val actions = remember(customer.id, callbacks) {
        listOf(
            PharmAction(
                label = "ประวัติ",
                icon = PharmIcons.SalesHistory,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenDetail(customer) },
            ),
            PharmAction(
                label = "แก้ไข",
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onOpenEdit(customer) },
            ),
            PharmAction(
                label = "ลบ",
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onDelete(customer) },
            ),
        )
    }
    PharmActionMenu(actions = actions)
}
