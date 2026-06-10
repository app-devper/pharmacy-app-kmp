package app.devper.pharm.ui.i18n.groups

object ValidationStringsTh : ValidationStrings {
    override val validationRequired: (String) -> String = { label -> "ต้องระบุ$label" }
    override val validationInvalidDate: (String) -> String = { label -> "${label}ไม่ถูกต้อง (รูปแบบ YYYY-MM-DD)" }
    override val validationNotANumber: (String) -> String = { label -> "${label}ต้องเป็นตัวเลข" }
    override val validationMustBePositive: (String) -> String = { label -> "${label}ต้องมากกว่า 0" }
    override val validationMustBeNonNegative: (String) -> String = { label -> "${label}ต้องไม่ติดลบ" }
    override val fieldDate = "วันที่"
    override val fieldQuantity = "จำนวน"
    override val fieldAmount = "ยอด"
    override val fieldValue = "มูลค่า"
    override val fieldDrug = "ยา"
    override val fieldDrugName = "ชื่อยา"
    override val fieldLotNumber = "เลขล็อต"
    override val fieldExpiryDate = "วันหมดอายุ"
    override val fieldUnit = "หน่วย"
    override val fieldPricePerUnit = "ราคาต่อหน่วย"
    override val fieldBalance = "ยอดคงเหลือ"
    override val fieldTotalValue = "มูลค่ารวม"
}
