package app.devper.pharm.presentation.offlinesync

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmListSkeleton

@Composable
fun OfflineSyncContent(
    state: OfflineSyncUiState,
    callbacks: OfflineSyncCallbacks = OfflineSyncCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OfflineSyncHeader(
            failedCount = state.failedCount,
            pendingCount = state.totalCount,
            onRefresh = callbacks.onRefresh,
            onSyncAll = callbacks.onSyncAll,
        )

        OfflineSyncMetricsRow(pending = state.pending)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            when {
                state.loading && state.pending.isEmpty() -> PharmListSkeleton()
                state.pending.isEmpty() -> EmptyOfflineSync()
                else -> OfflineSyncTable(pending = state.pending, callbacks = callbacks)
            }
        }
    }

    state.confirmDiscardId?.let {
        PharmModal(
            open = true,
            onDismiss = callbacks.onDismissCancel,
            title = "ลบรายการค้างซิงก์?",
            footer = {
                PharmButton(
                    label = "ยกเลิก",
                    onClick = callbacks.onDismissCancel,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                )
                PharmButton(
                    label = "ลบ",
                    onClick = callbacks.onConfirmCancel,
                    variant = PharmButtonVariant.Danger,
                    size = PharmButtonSize.Md,
                )
            },
        ) {
            Text(
                text = "บิลนี้จะถูกลบออกจากคิวภายในเครื่อง — ใช้เมื่อแน่ใจว่า " +
                    "backend รับบิลนี้ไปแล้วหรือไม่ต้องการให้ส่งซ้ำอีก",
                style = PharmText.body,
            )
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun OfflineSyncHeader(
    failedCount: Int,
    pendingCount: Int,
    onRefresh: () -> Unit,
    onSyncAll: () -> Unit,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "รายการค้างซิงก์", style = PharmText.h1)
            Text(
                text = "ตรวจสอบบิล offline ที่ยังไม่ได้ส่งเข้า backend",
                style = PharmText.meta.copy(color = t.colors.fgMuted),
            )
        }
        PharmButton(
            label = "รีเฟรช",
            onClick = onRefresh,
            variant = PharmButtonVariant.Secondary,
            size = PharmButtonSize.Md,
        )
        PharmButton(
            label = "ลองซิงก์ทั้งหมด",
            onClick = onSyncAll,
            variant = PharmButtonVariant.Primary,
            size = PharmButtonSize.Md,
            enabled = pendingCount > 0,
            leadingIcon = {
                Icon(
                    imageVector = PharmIcons.OfflineSync,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
        )
    }
}

@Composable
private fun EmptyOfflineSync() {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = PharmIcons.Check,
                contentDescription = null,
                tint = t.colors.successFg,
                modifier = Modifier.size(56.dp),
            )
            Text(
                text = "ไม่มีบิลค้างซิงก์",
                style = PharmText.h2,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = "ทุกบิลส่งเข้า backend แล้ว",
                style = PharmText.meta.copy(color = t.colors.fgMuted),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private val samplePending = listOf(
    PendingSale(
        id = "3a8f0001",
        clientRequestId = "req-3a8f0001-aaaa",
        payloadJson = "{}",
        enqueuedAt = 1716040920000L,
        lastError = null,
        attempts = 0,
    ),
    PendingSale(
        id = "3a8c0002",
        clientRequestId = "req-3a8c0002-bbbb",
        payloadJson = "{}",
        enqueuedAt = 1716035880000L,
        lastError = null,
        attempts = 1,
    ),
    PendingSale(
        id = "3a890003",
        clientRequestId = "req-3a890003-cccc",
        payloadJson = "{}",
        enqueuedAt = 1716033000000L,
        lastError = "lot_mismatch: PCM-25011 ถูกตัดหมด — backend retried with FEFO",
        attempts = 3,
    ),
)

@Preview
@Composable
private fun OfflineSyncContent_Loaded_Preview() {
    PharmacyTheme {
        OfflineSyncContent(state = OfflineSyncUiState(pending = samplePending))
    }
}

@Preview
@Composable
private fun OfflineSyncContent_Empty_Preview() {
    PharmacyTheme {
        OfflineSyncContent(state = OfflineSyncUiState(pending = emptyList()))
    }
}

@Preview
@Composable
private fun OfflineSyncContent_ConfirmDiscard_Preview() {
    PharmacyTheme {
        OfflineSyncContent(
            state = OfflineSyncUiState(
                pending = samplePending,
                confirmDiscardId = samplePending.first().id,
            ),
        )
    }
}

@Preview
@Composable
private fun OfflineSyncContent_WithFailures_Preview() {
    PharmacyTheme {
        OfflineSyncContent(
            state = OfflineSyncUiState(
                pending = samplePending,
                message = "ลบรายการค้างซิงก์แล้ว",
            ),
        )
    }
}
