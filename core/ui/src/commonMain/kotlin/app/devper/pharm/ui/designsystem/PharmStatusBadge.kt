package app.devper.pharm.ui.designsystem

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

fun PharmStatus.defaultLabel(): String = when (this) {
    PharmStatus.Pending     -> "รอดำเนินการ"
    PharmStatus.Done        -> "เสร็จสิ้น"
    PharmStatus.Voided      -> "ยกเลิก"
    PharmStatus.Active      -> "ใช้งาน"
    PharmStatus.Inactive    -> "ปิดใช้งาน"
    PharmStatus.Draft       -> "ร่าง"
    PharmStatus.Confirmed   -> "ยืนยันแล้ว"
    PharmStatus.Failed      -> "ล้มเหลว"
    PharmStatus.LowStock    -> "ใกล้หมด"
    PharmStatus.OutOfStock  -> "หมด"
    PharmStatus.Normal      -> "ปกติ"
    PharmStatus.Backordered -> "ค้างส่ง"
    PharmStatus.Vip         -> "VIP"
    PharmStatus.Returned    -> "คืนสินค้า"
}

@Composable
fun PharmStatusBadge(
    status: PharmStatus,
    modifier: Modifier = Modifier,
    label: String = status.defaultLabel(),
    size: PharmBadgeSize = PharmBadgeSize.Md,
) {
    PharmBadge(
        text = label,
        modifier = modifier,
        tone = status.tone(),
        size = size,
    )
}
