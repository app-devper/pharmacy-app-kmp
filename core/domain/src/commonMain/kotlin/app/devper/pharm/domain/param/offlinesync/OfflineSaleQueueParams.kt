package app.devper.pharm.domain.param.offlinesync

data class EnqueueOfflineSaleParam(
    val clientRequestId: String,
    val payloadJson: String,
)

data class MarkOfflineSaleFailedParam(
    val id: String,
    val error: String,
)
