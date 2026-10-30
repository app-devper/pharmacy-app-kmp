# Sign in from UM and with a passkey

Users signed in to UM open the pharmacy from UM's Apps page without signing in again (um-api ADR-0011), and on a front end served under UM's relying party domain they sign in with a passkey (um-api ADR-0010).

UM hands the app a one-time ticket at `/sso?ticket=…`. The web entry point reads it once, puts `/` back in the address bar and drops any session this browser already held, so the ticket alone decides who is signed in. `AuthRepository.exchangeTicket` trades the ticket for a session and then refuses it unless UM says the session is on the PHARMACY system; a refused or expired ticket shows why on the login screen, which stays usable. Passkey login runs UM's ceremony through a `PasskeyAuthenticator` contract bound per platform; only the browser implements it, and only when the page is served under `devper.app` (or `localhost`), because a passkey belongs to that relying party and `pharm-app.web.app` is outside it. Native platforms bind `Unsupported` and `None`, so the login screen there is unchanged.

A `401` on a request that carried no token is a refused sign-in, not an expired session: it no longer clears the stored token or raises the session-expired notice. UM's passkey error codes become typed exceptions so the screen can say what happened.
