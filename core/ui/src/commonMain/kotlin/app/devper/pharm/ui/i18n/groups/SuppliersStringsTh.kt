package app.devper.pharm.ui.i18n.groups

object SuppliersStringsTh : SuppliersStrings {
    override val suppliersListSubtitle = "จัดการข้อมูลผู้ขายและบริษัทคู่ค้า"
    override val suppliersSearchPlaceholder = "ค้นหาชื่อ / ผู้ติดต่อ / เบอร์โทร…"
    override val suppliersAddCta = "เพิ่มซัพพลายเออร์"
    override val suppliersListNotFound = "ไม่พบซัพพลายเออร์ตามที่ค้นหา"
    override val suppliersListEmpty = "ยังไม่มีซัพพลายเออร์"
    override val suppliersHeaderName = "ชื่อบริษัท / ร้านค้า"
    override val suppliersHeaderContact = "ผู้ติดต่อ"
    override val suppliersHeaderTaxId = "เลขผู้เสียภาษี"
    override val suppliersHeaderDetails = "รายละเอียด"
    override val suppliersDeleteConfirmTitle = "ลบซัพพลายเออร์?"
    override val suppliersDeleteConfirmMessage: (String) -> String = { name ->
        "ต้องการลบ \"$name\" ออกจากระบบหรือไม่ — ใบรับสินค้าเดิมจะยังคงเก็บชื่อนี้ไว้"
    }
    override val suppliersFormAddTitle = "เพิ่มผู้จัดจำหน่าย"
    override val suppliersFormEditTitle = "แก้ไขผู้จัดจำหน่าย"
    override val suppliersFormInfoSection = "ข้อมูลผู้จัดจำหน่าย"
    override val suppliersFormCompanyName = "ชื่อบริษัท / ผู้จัดจำหน่าย"
    override val suppliersFormCompanyPlaceholder = "เช่น บริษัท เอ บี ซี ฟาร์มา จำกัด"
    override val suppliersFormContactName = "ชื่อพนักงานขาย"
    override val suppliersFormAddress = "ที่อยู่"
    override val suppliersFormAddressPlaceholder = "บ้านเลขที่ / ถนน / ตำบล / อำเภอ / จังหวัด"
    override val suppliersFormTaxId = "เลขประจำตัวผู้เสียภาษี"
    override val suppliersFormNotesPlaceholder = "เงื่อนไขการสั่งซื้อ / ส่วนลด / รายละเอียดเพิ่มเติม"
}
