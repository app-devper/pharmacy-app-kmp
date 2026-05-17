package app.devper.pharm.data.network

data class ApiConfig(
    val umBaseUrl: String = "https://devper-um-1056670356976.asia-southeast1.run.app",
    val apiBaseUrl: String = "https://pharmacy-api-1056670356976.asia-southeast1.run.app",
) {
    val umAuthLogin: String get()  = "$umBaseUrl/api/um/v1/auth/login"
    val umAuthInfo: String get()   = "$umBaseUrl/api/um/v1/user/info"
    val umAuthLogout: String get() = "$umBaseUrl/api/um/v1/auth/logout"

    fun pharmacy(path: String): String = "$apiBaseUrl/api/pharmacy/v1${path.ensureLeadingSlash()}"

    fun umUser(path: String = ""): String = "$umBaseUrl/api/um/v1/user${path.ensureLeadingSlash()}"

    private fun String.ensureLeadingSlash(): String = if (isEmpty() || startsWith("/")) this else "/$this"
}
