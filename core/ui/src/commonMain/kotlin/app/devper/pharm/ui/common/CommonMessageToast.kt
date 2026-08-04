package app.devper.pharm.ui.common

import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localize

fun CommonUiStateMessage.toToast(s: PharmStrings): PharmToast = when (this) {
    is CommonUiStateMessage.Saved -> PharmToast.Success(localize(s))
    is CommonUiStateMessage.ExportDone -> PharmToast.Success(localize(s))
    is CommonUiStateMessage.ExportEmpty -> PharmToast.Warning(localize(s))
}
