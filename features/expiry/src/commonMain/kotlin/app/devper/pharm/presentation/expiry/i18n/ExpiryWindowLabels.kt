package app.devper.pharm.presentation.expiry.i18n

import app.devper.pharm.presentation.expiry.ExpiryWindow
import app.devper.pharm.ui.i18n.PharmStrings

fun ExpiryWindow.label(s: PharmStrings): String = when (this) {
    ExpiryWindow.Within30 -> s.expiryWindow30
    ExpiryWindow.Within60 -> s.expiryWindow60
    ExpiryWindow.Within90 -> s.expiryWindow90
    ExpiryWindow.Within180 -> s.expiryWindow180
    ExpiryWindow.ExpiredOnly -> s.expiryWindowExpired
}
