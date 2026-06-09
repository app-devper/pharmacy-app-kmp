package app.devper.pharm.presentation.help.exception

import app.devper.pharm.common.AppException

sealed class HelpException(message: String, cause: Throwable? = null) : AppException(message, cause) {

    sealed class Markdown(message: String, cause: Throwable? = null) : HelpException(message, cause) {
        class LoadFailed(cause: Throwable? = null) : Markdown("help.load_failed", cause)
    }
}
