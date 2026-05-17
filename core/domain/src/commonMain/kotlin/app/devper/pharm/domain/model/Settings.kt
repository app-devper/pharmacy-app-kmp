package app.devper.pharm.domain.model

data class Settings(
    val store: StoreInfo = StoreInfo(),
    val pharmacist: PharmacistInfo = PharmacistInfo(),
    val ky: KySettings = KySettings(),
    val timezone: String = "Asia/Bangkok",
)

data class StoreInfo(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val taxId: String = "",
)

data class PharmacistInfo(
    val name: String = "",
    val licenseNo: String = "",
)

data class KySettings(

    val skipAuto: Boolean = false,

    val defaultBuyerAddress: String = "",
)
