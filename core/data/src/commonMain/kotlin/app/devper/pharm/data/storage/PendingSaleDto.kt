package app.devper.pharm.data.storage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingSaleDto(
    @SerialName("id") val id: String,
    @SerialName("client_request_id") val clientRequestId: String,
    @SerialName("payload") val payload: String,
    @SerialName("enqueued_at") val enqueuedAt: Long,
    @SerialName("last_error") val lastError: String? = null,
    @SerialName("attempts") val attempts: Int = 0,
    @SerialName("ky") val ky: List<PendingKyFormDto> = emptyList(),
)

/** One queued KY form; [form] is `ky10`, `ky11` or `ky12` and selects the fields used. */
@Serializable
data class PendingKyFormDto(
    @SerialName("form") val form: String,
    @SerialName("sale_id") val saleId: String = "",
    @SerialName("date") val date: String,
    @SerialName("drug_name") val drugName: String,
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("qty") val qty: Int,
    @SerialName("unit") val unit: String = "",
    @SerialName("buyer_name") val buyerName: String = "",
    @SerialName("buyer_address") val buyerAddress: String = "",
    @SerialName("rx_no") val rxNo: String = "",
    @SerialName("doctor") val doctor: String = "",
    @SerialName("balance") val balance: Int = 0,
    @SerialName("purpose") val purpose: String = "",
    @SerialName("pharmacist") val pharmacist: String = "",
    @SerialName("patient_name") val patientName: String = "",
    @SerialName("hospital") val hospital: String = "",
    @SerialName("total_value") val totalValue: Double = 0.0,
    @SerialName("status") val status: String = "",
)
