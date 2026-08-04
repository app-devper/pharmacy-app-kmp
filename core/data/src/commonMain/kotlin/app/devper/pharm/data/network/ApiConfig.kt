package app.devper.pharm.data.network

data class ApiConfig(
    val apiBaseUrl: String = "https://api.devper.app",
) {
    val umAuthLogin: String get()  = "$apiBaseUrl/api/um/v1/auth/login"
    val umAuthInfo: String get()   = "$apiBaseUrl/api/um/v1/user/info"
    val umAuthLogout: String get() = "$apiBaseUrl/api/um/v1/auth/logout"

    fun pharmacy(path: String): String = "$apiBaseUrl/api/pharmacy/v1${path.ensureLeadingSlash()}"

    fun umUser(path: String = ""): String = "$apiBaseUrl/api/um/v1/user${path.ensureLeadingSlash()}"

    private fun String.ensureLeadingSlash(): String = if (isEmpty() || startsWith("/")) this else "/$this"
}

fun localQaApiBaseUrl(pageHost: String, rawQuery: String): String? {
    if (pageHost.lowercase() !in setOf("localhost", "127.0.0.1", "::1")) return null
    return rawQuery
        .removePrefix("?")
        .split("&")
        .mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            parts.takeIf { it.size == 2 && it[0] == "apiBaseUrl" }?.get(1)
        }
        .firstOrNull()
        ?.trim()
        ?.trimEnd('/')
        ?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
}
