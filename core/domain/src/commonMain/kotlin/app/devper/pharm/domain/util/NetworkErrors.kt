package app.devper.pharm.domain.util

fun Throwable.looksLikeNetworkError(): Boolean {

    var cur: Throwable? = this
    var depth = 0
    while (cur != null && depth < 6) {
        val name = cur::class.simpleName.orEmpty()
        if (NETWORK_CLASS_HINTS.any { name.contains(it, ignoreCase = true) }) return true
        val msg = cur.message.orEmpty()
        if (NETWORK_MESSAGE_HINTS.any { msg.contains(it, ignoreCase = true) }) return true
        cur = cur.cause
        depth++
    }
    return false
}

private val NETWORK_CLASS_HINTS = listOf(
    "ConnectException",
    "ConnectTimeoutException",
    "SocketTimeoutException",
    "UnknownHost",
    "HttpRequestTimeout",
    "IOException",
    "NoTransformationFound",
)

private val NETWORK_MESSAGE_HINTS = listOf(
    "Failed to connect",
    "Network is unreachable",
    "Connection refused",
    "Connection reset",
    "request timed out",
    "Could not resolve",
)
