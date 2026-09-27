package app.devper.pharm.domain.param.offlinesync

import app.devper.pharm.domain.model.KyForm

data class EnqueueOfflineSaleParam(
    val clientRequestId: String,
    val payloadJson: String,
    val kyForms: List<KyForm> = emptyList(),
)

data class MarkOfflineSaleFailedParam(
    val id: String,
    val error: String,
)
