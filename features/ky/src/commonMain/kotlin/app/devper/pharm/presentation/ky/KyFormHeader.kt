package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.KyFormType

internal val KyFormType.number: Int
    get() = when (this) {
        KyFormType.Ky9 -> 9
        KyFormType.Ky10 -> 10
        KyFormType.Ky11 -> 11
        KyFormType.Ky12 -> 12
    }

internal data class KyFormMeta(
    val title: String,
    val subtitle: String,
)

internal fun kyFormMeta(form: KyFormType): KyFormMeta = when (form) {
    KyFormType.Ky9 -> KyFormMeta(
        title = "ขย.9 — บัญชีการซื้อยา",
        subtitle = "บัญชีแสดงรายการซื้อยาและผลิตภัณฑ์สุขภาพ",
    )
    KyFormType.Ky10 -> KyFormMeta(
        title = "ขย.10 — บัญชียาควบคุมพิเศษ",
        subtitle = "บัญชีแสดงการขายยาควบคุมพิเศษ",
    )
    KyFormType.Ky11 -> KyFormMeta(
        title = "ขย.11 — บัญชียาอันตราย",
        subtitle = "บัญชีแสดงการขายยาอันตราย",
    )
    KyFormType.Ky12 -> KyFormMeta(
        title = "ขย.12 — บัญชียาที่ต้องใช้ใบสั่งแพทย์",
        subtitle = "บัญชีแสดงการขายยาที่ต้องใช้ใบสั่งแพทย์",
    )
}
