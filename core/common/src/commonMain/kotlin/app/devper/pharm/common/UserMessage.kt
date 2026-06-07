package app.devper.pharm.common

fun Throwable.userMessageOr(fallback: String): String =
    if (this is AppException) message ?: fallback else fallback
