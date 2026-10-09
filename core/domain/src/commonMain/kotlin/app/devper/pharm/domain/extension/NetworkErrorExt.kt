package app.devper.pharm.domain.extension

import app.devper.pharm.common.AppException
import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.IdentityUnavailableException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.ServerException

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

fun Throwable.looksLikeTemporaryOutage(): Boolean =
    this is IdentityUnavailableException || looksLikeNetworkError()

fun Throwable.isTemporaryDeliveryFailure(): Boolean = when (this) {
    is NetworkException, is IdentityUnavailableException, is AuthException, is ForbiddenException -> true
    is ServerException -> (statusCode ?: 500) >= 500
    is AppException -> false
    else -> looksLikeNetworkError()
}
