package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class PharmStatus {
    Pending,
    Done,
    Voided,
    Active,
    Inactive,
    Draft,
    Confirmed,
    Failed,
    LowStock,
    OutOfStock,
    Normal,
    Backordered,
    Vip,
    Returned,
}

fun PharmStatus.tone(): PharmBadgeTone = when (this) {
    PharmStatus.Pending     -> PharmBadgeTone.Amber
    PharmStatus.Done        -> PharmBadgeTone.Green
    PharmStatus.Voided      -> PharmBadgeTone.Red
    PharmStatus.Active      -> PharmBadgeTone.Green
    PharmStatus.Inactive    -> PharmBadgeTone.Gray
    PharmStatus.Draft       -> PharmBadgeTone.Amber
    PharmStatus.Confirmed   -> PharmBadgeTone.Green
    PharmStatus.Failed      -> PharmBadgeTone.Red
    PharmStatus.LowStock    -> PharmBadgeTone.Amber
    PharmStatus.OutOfStock  -> PharmBadgeTone.Red
    PharmStatus.Normal      -> PharmBadgeTone.Green
    PharmStatus.Backordered -> PharmBadgeTone.Red
    PharmStatus.Vip         -> PharmBadgeTone.Purple
    PharmStatus.Returned    -> PharmBadgeTone.Blue
}

fun PharmStatus.label(s: PharmStrings): String = when (this) {
    PharmStatus.Pending     -> s.commonStatusPending
    PharmStatus.Done        -> s.commonStatusDone
    PharmStatus.Voided      -> s.commonStatusVoided
    PharmStatus.Active      -> s.commonStatusActive
    PharmStatus.Inactive    -> s.commonStatusInactive
    PharmStatus.Draft       -> s.commonStatusDraft
    PharmStatus.Confirmed   -> s.commonStatusConfirmed
    PharmStatus.Failed      -> s.commonStatusFailed
    PharmStatus.LowStock    -> s.commonStatusLowStock
    PharmStatus.OutOfStock  -> s.commonStatusOutOfStock
    PharmStatus.Normal      -> s.commonStatusNormal
    PharmStatus.Backordered -> s.commonStatusBackordered
    PharmStatus.Vip         -> s.commonStatusVip
    PharmStatus.Returned    -> s.commonStatusReturned
}

@Composable
fun PharmStatusBadge(
    status: PharmStatus,
    modifier: Modifier = Modifier,
    label: String = status.label(pharmStrings),
    size: PharmBadgeSize = PharmBadgeSize.Md,
) {
    PharmBadge(
        text = label,
        modifier = modifier,
        tone = status.tone(),
        size = size,
    )
}
