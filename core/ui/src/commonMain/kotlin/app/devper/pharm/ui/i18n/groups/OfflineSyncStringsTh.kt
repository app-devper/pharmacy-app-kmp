package app.devper.pharm.ui.i18n.groups

object OfflineSyncStringsTh : OfflineSyncStrings {
    override val offlineSyncSubtitle = "ตรวจสอบบิล offline ที่ยังไม่ได้ส่งเข้า backend"
    override val offlineSyncRetryAllCta = "ลองซิงก์ทั้งหมด"
    override val offlineSyncEmptyTitle = "ไม่มีบิลค้างซิงก์"
    override val offlineSyncEmpty = "ทุกบิลส่งเข้า backend แล้ว"
    override val offlineSyncMetricsTotal = "รายการค้างทั้งหมด"
    override val offlineSyncMetricsLocation = "ใน IndexedDB"
    override val offlineSyncMetricsAttempts = "ความพยายามสะสม"
    override val offlineSyncMetricsAttemptsSuffix = "ครั้งสะสม"
    override val offlineSyncMetricsFailed = "ซิงก์ล้มเหลว"
    override val offlineSyncStatusFailed = "ล้มเหลว"
    override val offlineSyncStatusPending = "รอซิงก์"
    override val offlineSyncStatusRetry = "รอ retry"
    override val offlineSyncAttemptsLabel: (Int) -> String = { attempts -> "ลอง $attempts ครั้ง" }
    override val offlineSyncRetryRowCta = "ลองส่งใหม่"
    override val offlineSyncDeleteConfirmTitle = "ลบรายการค้างซิงก์?"
    override val offlineSyncDeleteConfirmMessage =

        "บิลนี้จะถูกลบออกจากคิวภายในเครื่อง — ใช้เมื่อแน่ใจว่า " +
        "backend รับบิลนี้ไปแล้วหรือไม่ต้องการให้ส่งซ้ำอีก"
    override val offlineSyncLoadFailed = "โหลดรายการค้างซิงก์ไม่สำเร็จ"
    override val offlineSyncSyncPartialFailed: (Int, Int) -> String = { failed, total -> "ส่งบิลไม่สำเร็จ $failed จาก $total รายการ" }
    override val offlineSyncRetryFailed: (String) -> String = { billId -> "ส่งบิล $billId ไม่สำเร็จ" }
    override val offlineSyncDiscardFailed = "ลบรายการไม่สำเร็จ"
    override val offlineSyncRefreshed = "ดึงสถานะคิวล่าสุดแล้ว"
    override val offlineSyncSyncStarted: (Int) -> String = { count -> "เริ่มซิงก์ $count รายการ" }
    override val offlineSyncRetryStarted: (String) -> String = { billId -> "เริ่มลองส่งบิล $billId ใหม่แล้ว" }
    override val offlineSyncDiscarded = "ลบรายการค้างซิงก์แล้ว"
}
