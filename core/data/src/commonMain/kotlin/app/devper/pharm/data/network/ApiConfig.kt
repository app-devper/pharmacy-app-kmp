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
