package app.devper.pharm.domain.extension

import app.devper.pharm.common.IdentityUnavailableException

private val NETWORK_CLASS_HINTS = listOf(
    "ConnectException",
    "ConnectTimeoutException",
    "SocketTimeoutException",
    "SocketException",
    "UnknownHost",
    "HttpRequestTimeout",
    "IOException",
    "EOFException",
    "NoTransformationFound",
)

private val NETWORK_MESSAGE_HINTS = listOf(
    "Failed to connect",
    "Failed to fetch",
    "Network is unreachable",
    "Connection refused",
    "Connection reset",
    "request timed out",
    "Could not resolve",
)

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

/**
 * True when the request never reached a decision on the server and is worth
 * retrying later: a network failure, or the pharmacy API being unable to
 * confirm the session (ADR-0016). A sale that fails this way stays pending.
 */
fun Throwable.looksLikeTemporaryOutage(): Boolean =
    this is IdentityUnavailableException || looksLikeNetworkError()
