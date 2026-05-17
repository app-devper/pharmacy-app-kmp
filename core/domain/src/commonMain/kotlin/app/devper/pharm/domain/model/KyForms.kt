package app.devper.pharm.domain.model

sealed interface KyForm {
    val date: String
    val drugName: String
    val regNo: String
    val qty: Int
    val unit: String

    data class Ky10(
        override val date: String,
        override val drugName: String,
        override val regNo: String,
        override val qty: Int,
        override val unit: String,
        val buyerName: String,
        val buyerAddress: String,
        val rxNo: String,
        val doctor: String,
        val balance: Int,
    ) : KyForm

    data class Ky11(
        override val date: String,
        override val drugName: String,
        override val regNo: String,
        override val qty: Int,
        override val unit: String,
        val buyerName: String,
        val purpose: String,
        val pharmacist: String,
    ) : KyForm

    data class Ky12(
        override val date: String,
        override val drugName: String,
        override val regNo: String,
        override val qty: Int,
        override val unit: String,
        val rxNo: String,
        val patientName: String,
        val doctor: String,
        val hospital: String,
        val totalValue: Double,
        val status: String,
    ) : KyForm
}

data class KyCaptureFields(

    val ky10BuyerName: String = "",
    val ky10BuyerAddress: String = "",
    val ky10RxNo: String = "",
    val ky10Doctor: String = "",
    val ky10Balance: Int = 0,

    val ky11BuyerName: String = "",
    val ky11Purpose: String = "",
    val ky11Pharmacist: String = "",

    val ky12RxNo: String = "",
    val ky12PatientName: String = "",
    val ky12Doctor: String = "",
    val ky12Hospital: String = "",
    val ky12Status: String = "จ่ายแล้ว",
)

data class KyRequired(
    val ky10: List<CartLine> = emptyList(),
    val ky11: List<CartLine> = emptyList(),
    val ky12: List<CartLine> = emptyList(),
) {
    val isEmpty: Boolean get() = ky10.isEmpty() && ky11.isEmpty() && ky12.isEmpty()
    val needsKy10: Boolean get() = ky10.isNotEmpty()
    val needsKy11: Boolean get() = ky11.isNotEmpty()
    val needsKy12: Boolean get() = ky12.isNotEmpty()
}

data class KySubmissionResult(
    val attempted: Int,
    val failed: List<String>,
) {
    val allOk: Boolean get() = failed.isEmpty()
    val anyFailed: Boolean get() = failed.isNotEmpty()
}
