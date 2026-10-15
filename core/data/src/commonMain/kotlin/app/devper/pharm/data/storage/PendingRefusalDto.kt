package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.model.KyRefusal
import app.devper.pharm.domain.model.PendingSaleRefusal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingRefusalDto(
    @SerialName("kind") val kind: String,
    @SerialName("reason") val reason: String = "",
    @SerialName("ky") val ky: List<PendingKyRefusalDto> = emptyList(),
)

@Serializable
data class PendingKyRefusalDto(
    @SerialName("form") val form: String,
    @SerialName("drug_name") val drugName: String,
    @SerialName("reason") val reason: String,
)

internal fun PendingSaleRefusal.toDto(): PendingRefusalDto = when (this) {
    is PendingSaleRefusal.Server -> PendingRefusalDto(kind = "server", reason = reason)
    is PendingSaleRefusal.Outage -> PendingRefusalDto(kind = "outage", reason = detail)
    is PendingSaleRefusal.Unreadable -> PendingRefusalDto(kind = "unreadable", reason = detail)
    is PendingSaleRefusal.KyRefused -> PendingRefusalDto(
        kind = "ky",
        ky = forms.map { PendingKyRefusalDto(form = it.form.wire, drugName = it.drugName, reason = it.reason) },
    )
}

internal fun PendingRefusalDto.toDomain(): PendingSaleRefusal = when (kind) {
    "outage" -> PendingSaleRefusal.Outage(reason)
    "unreadable" -> PendingSaleRefusal.Unreadable(reason)
    "ky" -> PendingSaleRefusal.KyRefused(ky.map { KyRefusal(kyFormTypeOf(it.form), it.drugName, it.reason) })
    else -> PendingSaleRefusal.Server(reason)
}

internal fun legacyRefusal(lastError: String, kyPending: Boolean): PendingSaleRefusal {
    if (!kyPending) return PendingSaleRefusal.Server(lastError)
    val forms = lastError.lines().filter { it.isNotBlank() }.map { line ->
        val parts = line.split(':', limit = 3)
        if (parts.size == 3) KyRefusal(kyFormTypeOf(parts[0]), parts[1], parts[2])
        else KyRefusal(KyFormType.Ky10, "", line)
    }
    return PendingSaleRefusal.KyRefused(forms)
}

private fun kyFormTypeOf(wire: String): KyFormType =
    KyFormType.entries.firstOrNull { it.wire == wire } ?: KyFormType.Ky10
