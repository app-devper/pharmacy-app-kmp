package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.format.formatBaht
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.theme.tabular
import org.koin.compose.viewmodel.koinViewModel
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDetailScreen(
    importId: String,
    onBack: () -> Unit,
    onEdit: (id: String) -> Unit,
    viewModel: ImportDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(importId) { viewModel.init(importId) }
    LaunchedEffect(state.closed) { if (state.closed) onBack() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.po?.docNo ?: "ใบรับสินค้า",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                actions = {
                    state.po?.let { po ->
                        IconButton(onClick = { onEdit(po.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "แก้ไข",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            state.po?.let { po -> ActionBar(po = po, state = state, vm = viewModel) }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when {
                state.loading && state.po == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress()
                }
                state.po == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("ไม่พบใบรับสินค้า", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> Body(po = state.po!!)
            }
        }
    }

    if (state.confirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::cancelConfirm,
            title = { Text("ยืนยันรับสินค้า?") },
            text = {
                Text(
                    "เมื่อยืนยันแล้วระบบจะเพิ่มล็อต + อัปเดตสต็อก + บันทึก ขย.9 — ไม่สามารถยกเลิกได้",
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmNow, enabled = !state.confirming) {
                    Text("ยืนยัน", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelConfirm, enabled = !state.confirming) {
                    Text("ยกเลิก")
                }
            },
        )
    }
    if (state.deleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            title = { Text("ลบใบรับสินค้า?") },
            text = {
                Text(
                    "ใบนี้ยังไม่ได้ยืนยัน — ลบแล้วจะไม่สามารถกู้คืนได้ " +
                        "(สต็อกและล็อตยังไม่ถูกแตะต้อง)",
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::deleteNow, enabled = !state.deleting) {
                    Text("ลบ", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDelete, enabled = !state.deleting) {
                    Text("ยกเลิก")
                }
            },
        )
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}

@Composable
private fun Body(po: PurchaseOrder) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item("header") { HeaderBlock(po) }
        item("section") {
            Text(
                text = "รายการสินค้า · ${po.itemCount} รายการ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            )
        }
        items(po.items) { item -> ItemRow(item) }
    }
}

@Composable
private fun HeaderBlock(po: PurchaseOrder) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusChip(po.status)
                Text(
                    text = "รวม ${formatBahtCurrency(po.totalCost)}",
                    style = MaterialTheme.typography.titleMedium.tabular(),
                    fontWeight = FontWeight.SemiBold,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            DetailRow("ผู้จัดจำหน่าย", po.supplier.ifBlank { "-" })
            DetailRow("เลขที่ Invoice", po.invoiceNo.ifBlank { "-" })
            DetailRow("วันที่รับ", po.receiveDate.take(10).ifBlank { "-" })
            if (po.notes.isNotBlank()) DetailRow("หมายเหตุ", po.notes)
            DetailRow("สร้างเมื่อ", po.createdAt.take(19).replace('T', ' '))
            po.confirmedAt?.let {
                DetailRow("ยืนยันเมื่อ", it.take(19).replace('T', ' '))
            }
        }
    }
}

@Composable
private fun StatusChip(status: PurchaseOrderStatus) {
    val (container, content, label) = when (status) {
        PurchaseOrderStatus.Draft     -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "ร่าง",
        )
        PurchaseOrderStatus.Confirmed -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "ยืนยันแล้ว",
        )
    }
    AssistChip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = container,
            disabledLabelColor = content,
        ),
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ItemRow(item: PurchaseOrderItem) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.drugName.ifBlank { "(ไม่ระบุยา)" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "ล็อต ${item.lotNumber} · หมดอายุ ${item.expiryDate}",
                    style = MaterialTheme.typography.labelSmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.qty} ชิ้น",
                    style = MaterialTheme.typography.titleSmall.tabular(),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "@${formatBaht(item.costPrice)}",
                    style = MaterialTheme.typography.labelSmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ActionBar(
    po: PurchaseOrder,
    state: ImportDetailUiState,
    vm: ImportDetailViewModel,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (po.status == PurchaseOrderStatus.Draft) {
                OutlinedButton(
                    onClick = vm::askDelete,
                    enabled = !state.confirming && !state.deleting,
                ) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
                    Text("ลบ", modifier = Modifier.padding(start = 6.dp))
                }
                Button(
                    onClick = vm::askConfirm,
                    enabled = !state.confirming && !state.deleting && po.itemCount > 0,
                    modifier = Modifier.weight(1f),
                ) {
                    if (state.confirming) {
                        PharmCircularProgress(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                        )
                    } else {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                        Text("ยืนยันรับสินค้า", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "ยืนยันแล้ว — ใบนี้ถูกบันทึกในสต็อกเรียบร้อย",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}
