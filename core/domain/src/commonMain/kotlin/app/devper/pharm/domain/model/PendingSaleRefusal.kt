package app.devper.pharm.domain.model

sealed interface PendingSaleRefusal {
    data class Server(val reason: String) : PendingSaleRefusal
    data class Outage(val detail: String) : PendingSaleRefusal
    data class KyRefused(val forms: List<KyRefusal>) : PendingSaleRefusal
    data class Unreadable(val detail: String) : PendingSaleRefusal
}

data class KyRefusal(val form: KyFormType, val drugName: String, val reason: String)
