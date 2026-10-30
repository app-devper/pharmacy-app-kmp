package app.devper.pharm.presentation.auth.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.common.LaunchExpiredException
import app.devper.pharm.common.LaunchRefusedException
import app.devper.pharm.common.PasskeyCancelledException
import app.devper.pharm.common.PasskeyExpiredException
import app.devper.pharm.common.PasskeyOtherAccountException
import app.devper.pharm.common.PasskeyRejectedException
import app.devper.pharm.common.PasskeysUnavailableException
import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.presentation.auth.exception.LoginUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeLogin(s: PharmStrings): String = when (this) {
    is LoginUiStateError.RequiredFields -> s.loginRequiredFields
    is LoginUiStateError.LoginFailed -> s.loginFailed
    is LoginUiStateError.PasskeyFailed -> localizePasskeyFailure(cause, s)
    is LoginUiStateError.LaunchFailed -> localizeLaunchFailure(cause, s)
    else -> localizeCommon(s)
}

private fun localizePasskeyFailure(cause: Throwable?, s: PharmStrings): String = when (cause) {
    is PasskeyCancelledException -> s.loginPasskeyCancelled
    is PasskeyRejectedException -> s.loginPasskeyRejected
    is PasskeyOtherAccountException -> s.loginPasskeyOtherAccount
    is PasskeyExpiredException -> s.loginPasskeyExpired
    is PasskeysUnavailableException, is UnsupportedPlatformException -> s.loginPasskeyUnavailable
    is AppException -> cause.localizeCommon(s)
    else -> s.loginPasskeyFailed
}

private fun localizeLaunchFailure(cause: Throwable?, s: PharmStrings): String = when (cause) {
    is LaunchExpiredException -> s.loginFromUmExpired
    is LaunchRefusedException -> s.loginFromUmRefused
    is AppException -> cause.localizeCommon(s)
    else -> s.loginFromUmExpired
}
