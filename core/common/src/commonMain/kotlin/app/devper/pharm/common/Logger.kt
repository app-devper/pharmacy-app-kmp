package app.devper.pharm.common

interface Logger {
    fun warn(tag: String, message: String, cause: Throwable? = null)
    fun debug(tag: String, message: String) = Unit
}

class PrintlnLogger : Logger {
    override fun warn(tag: String, message: String, cause: Throwable?) {
        val suffix = cause?.message?.takeIf { it.isNotBlank() }?.let { " — $it" } ?: ""
        println("[$tag] WARN: $message$suffix")
    }
}
