package app.devper.pharm.presentation.offlinesync.i18n

import app.devper.pharm.domain.model.PendingSaleRefusal
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.navTitle

fun PendingSaleRefusal.localize(s: PharmStrings): String = when (this) {
    is PendingSaleRefusal.Server -> reason
    is PendingSaleRefusal.Outage -> s.offlineSyncRefusalOutage(detail)
    is PendingSaleRefusal.Unreadable -> detail
    is PendingSaleRefusal.KyRefused -> forms.joinToString("\n") { s.offlineSyncRefusalKy(it.form.navTitle(s), it.drugName, it.reason) }
}
