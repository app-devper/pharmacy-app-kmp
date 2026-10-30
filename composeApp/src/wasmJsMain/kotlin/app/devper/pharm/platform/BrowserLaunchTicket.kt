package app.devper.pharm.platform

import app.devper.pharm.common.platform.LaunchTicket

private const val MAX_TICKET_LENGTH = 128

class BrowserLaunchTicket : LaunchTicket {
    private var pending: String? = readAndClearTicket()?.takeIf { it.isNotEmpty() && it.length <= MAX_TICKET_LENGTH }

    override val isPending: Boolean get() = pending != null

    override fun peek(): String? = pending

    override fun consume() {
        pending = null
    }
}

private fun readAndClearTicket(): String? = js(
    """
    (function () {
        if (window.location.pathname !== '/sso') return null;
        var ticket = new URLSearchParams(window.location.search).get('ticket');
        window.history.replaceState(null, '', '/');
        return ticket;
    })()
    """,
)
