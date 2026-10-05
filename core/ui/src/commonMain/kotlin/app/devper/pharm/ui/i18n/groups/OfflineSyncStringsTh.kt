package app.devper.pharm.ui.i18n.groups

object OfflineSyncStringsTh : OfflineSyncStrings {
    override val offlineSyncSubtitle = "ตรวจสอบบิล offline ที่ยังไม่ได้ส่งเข้า backend"
    override val offlineSyncRetryAllCta = "ลองซิงก์ทั้งหมด"
    override val offlineSyncEmptyTitle = "ไม่มีบิลค้างซิงก์"
    override val offlineSyncEmpty = "ทุกบิลส่งเข้า backend แล้ว"
    override val offlineSyncMetricsTotal = "รายการค้างทั้งหมด"
    override val offlineSyncMetricsLocation = "ในเครื่องนี้"
    override val offlineSyncMetricsFailed = "ซิงก์ล้มเหลว"
    override val offlineSyncStatusFailed = "ล้มเหลว"
    override val offlineSyncStatusPending = "รอซิงก์"
    override val offlineSyncStatusSyncing = "กำลังซิงก์…"
    override val offlineSyncStatusRetry = "รอ retry"
    override val offlineSyncAttemptsLabel: (Int) -> String = { attempts -> "ลอง $attempts ครั้ง" }
    override val offlineSyncRetryRowCta = "ลองส่งใหม่"
    override val offlineSyncDeleteConfirmTitle = "ลบข้อมูลที่เสียหาย?"
    override val offlineSyncDeleteConfirmMessage =

        "ข้อมูลที่อ่านไม่ได้นี้จะถูกลบออกจากเครื่อง — " +
        "ส่งออกไฟล์เก็บไว้ก่อนแล้ว จึงลบได้"
    override val offlineSyncLoadFailed = "โหลดรายการค้างซิงก์ไม่สำเร็จ"
    override val offlineSyncSyncPartialFailed: (Int, Int) -> String = { failed, total -> "ส่งบิลไม่สำเร็จ $failed จาก $total รายการ" }
    override val offlineSyncRetryFailed: (String) -> String = { billId -> "ส่งบิล $billId ไม่สำเร็จ" }
    override val offlineSyncDiscardFailed = "ลบรายการไม่สำเร็จ"
    override val offlineSyncSyncStarted: (Int) -> String = { count -> "เริ่มซิงก์ $count รายการ" }
    override val offlineSyncRetryStarted: (String) -> String = { billId -> "เริ่มลองส่งบิล $billId ใหม่แล้ว" }
    override val offlineSyncDiscarded = "ลบรายการค้างซิงก์แล้ว"
    override val offlineSyncStatusConflict = "ถูกปฏิเสธ"
    override val offlineSyncStatusKyPending = "ขย. ค้าง"
    override val offlineSyncStatusDamaged = "ข้อมูลเสียหาย"
    override val offlineSyncMetricsNeedsAction = "ต้องจัดการ"
    override val offlineSyncNeedsActionSub = "ไม่ส่งซ้ำอัตโนมัติ"
    override val offlineSyncKyPendingBill: (String) -> String = { billNo -> "บันทึกบิล $billNo แล้ว แต่ ขย. ถูกปฏิเสธ" }
    override val offlineSyncDamagedHint = "อ่านข้อมูลบิลนี้ไม่ได้ ให้ส่งออกไฟล์เก็บไว้ก่อน"
    override val offlineSyncAbandonCta = "ยกเลิกบิล"
    override val offlineSyncCloseKyCta = "ปิดเรื่อง ขย."
    override val offlineSyncExportCta = "ส่งออกไฟล์"
    override val offlineSyncAbandonTitle = "ยกเลิกบิลที่ค้าง?"
    override val offlineSyncAbandonMessage =
        "บิลนี้จะไม่ถูกบันทึกเป็นยอดขาย และระบบจะเก็บข้อมูลบิลกับเหตุผลไว้ตรวจสอบย้อนหลัง"
    override val offlineSyncCloseKyTitle = "ปิดเรื่อง ขย. ที่ค้าง?"
    override val offlineSyncCloseKyMessage =
        "ขย. ที่ถูกปฏิเสธจะไม่ถูกบันทึก และระบบจะเก็บแบบฟอร์มกับเหตุผลไว้ตรวจสอบย้อนหลัง"
    override val offlineSyncReasonPlaceholder = "เหตุผล (จำเป็น)"
    override val offlineSyncDiscardDamagedMessage = "ลบข้อมูลที่เสียหายแล้ว"
    override val offlineSyncAbandonFailed = "ยกเลิกไม่สำเร็จ"
    override val offlineSyncExportFailed = "ส่งออกไฟล์ไม่สำเร็จ"
    override val offlineSyncAbandoned = "บันทึกการยกเลิกแล้ว"
    override val offlineSyncExported: (String) -> String = { path -> "บันทึกไฟล์แล้ว: $path" }
    override val offlineSyncRecorded = "ส่งบิลเข้าระบบแล้ว"
    override val offlineSyncStillNotRecorded: (String) -> String = { billId -> "บิล $billId ยังส่งไม่ผ่าน ดูเหตุผลที่รายการ" }
}
