package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ky10Request(
    @SerialName("date") val date: String,
    @SerialName("drug_name") val drugName: String,
    @SerialName("reg_no") val regNo: String,
    @SerialName("qty") val qty: Int,
    @SerialName("unit") val unit: String,
    @SerialName("buyer_name") val buyerName: String,
    @SerialName("buyer_address") val buyerAddress: String,
    @SerialName("rx_no") val rxNo: String,
    @SerialName("doctor") val doctor: String,
    @SerialName("balance") val balance: Int,
)

@Serializable
data class Ky11Request(
    @SerialName("date") val date: String,
    @SerialName("drug_name") val drugName: String,
    @SerialName("reg_no") val regNo: String,
    @SerialName("qty") val qty: Int,
    @SerialName("unit") val unit: String,
    @SerialName("buyer_name") val buyerName: String,
    @SerialName("purpose") val purpose: String,
    @SerialName("pharmacist") val pharmacist: String,
)

@Serializable
data class Ky12Request(
    @SerialName("date") val date: String,
    @SerialName("rx_no") val rxNo: String,
    @SerialName("patient_name") val patientName: String,
    @SerialName("doctor") val doctor: String,
    @SerialName("hospital") val hospital: String,
    @SerialName("drug_name") val drugName: String,
    @SerialName("qty") val qty: Int,
    @SerialName("unit") val unit: String,
    @SerialName("total_value") val totalValue: Double,
    @SerialName("status") val status: String,
)

@Serializable
data class Ky9Request(
    @SerialName("date") val date: String,
    @SerialName("drug_name") val drugName: String,
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("unit") val unit: String,
    @SerialName("qty") val qty: Int,
    @SerialName("price_per_unit") val pricePerUnit: Double,
    @SerialName("seller") val seller: String = "",
    @SerialName("invoice_no") val invoiceNo: String = "",
)

@Serializable
data class Ky9Dto(
    @SerialName("id") val id: String,
    @SerialName("date") val date: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("unit") val unit: String = "",
    @SerialName("qty") val qty: Int = 0,
    @SerialName("price_per_unit") val pricePerUnit: Double = 0.0,
    @SerialName("total_value") val totalValue: Double = 0.0,
    @SerialName("seller") val seller: String = "",
    @SerialName("invoice_no") val invoiceNo: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class Ky10Dto(
    @SerialName("id") val id: String,
    @SerialName("date") val date: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("qty") val qty: Int = 0,
    @SerialName("unit") val unit: String = "",
    @SerialName("buyer_name") val buyerName: String = "",
    @SerialName("buyer_address") val buyerAddress: String = "",
    @SerialName("rx_no") val rxNo: String = "",
    @SerialName("doctor") val doctor: String = "",
    @SerialName("balance") val balance: Int = 0,
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class Ky11Dto(
    @SerialName("id") val id: String,
    @SerialName("date") val date: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("reg_no") val regNo: String = "",
    @SerialName("qty") val qty: Int = 0,
    @SerialName("unit") val unit: String = "",
    @SerialName("buyer_name") val buyerName: String = "",
    @SerialName("purpose") val purpose: String = "",
    @SerialName("pharmacist") val pharmacist: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class Ky12Dto(
    @SerialName("id") val id: String,
    @SerialName("date") val date: String = "",
    @SerialName("rx_no") val rxNo: String = "",
    @SerialName("patient_name") val patientName: String = "",
    @SerialName("doctor") val doctor: String = "",
    @SerialName("hospital") val hospital: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("qty") val qty: Int = 0,
    @SerialName("unit") val unit: String = "",
    @SerialName("total_value") val totalValue: Double = 0.0,
    @SerialName("status") val status: String = "",
    @SerialName("created_at") val createdAt: String = "",
)
