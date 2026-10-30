@file:OptIn(ExperimentalWasmJsInterop::class)

package app.devper.pharm.platform

import app.devper.pharm.common.PasskeyCancelledException
import app.devper.pharm.common.PasskeyRejectedException
import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.common.platform.PasskeyAuthenticator
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.Promise

private const val RELYING_PARTY_DOMAIN = "devper.app"

private fun passkeysAvailable(): Boolean = js(
    """typeof window.PublicKeyCredential === 'function'
        && typeof PublicKeyCredential.parseRequestOptionsFromJSON === 'function'""",
)

private fun getCredential(options: String): Promise<JsString> = js(
    """navigator.credentials
        .get({ publicKey: PublicKeyCredential.parseRequestOptionsFromJSON(JSON.parse(options).publicKey) })
        .then(function (c) { return JSON.stringify({ ok: true, value: JSON.stringify(c.toJSON()) }); })
        .catch(function (e) { return JSON.stringify({ ok: false, error: (e && e.name) || 'Error' }); })""",
)

@Serializable
private data class Outcome(val ok: Boolean, val value: String? = null, val error: String? = null)

private val outcomeJson = Json { ignoreUnknownKeys = true }

class BrowserPasskeyAuthenticator : PasskeyAuthenticator {
    override val isSupported: Boolean = passkeysAvailable() && servedUnderRelyingParty(window.location.hostname)

    override suspend fun get(optionsJson: String): String = settle(getCredential(optionsJson).await<JsString>().toString())

    private fun settle(raw: String): String {
        val outcome = outcomeJson.decodeFromString<Outcome>(raw)
        outcome.value?.takeIf { outcome.ok }?.let { return it }
        throw when (outcome.error) {
            "NotAllowedError", "AbortError" -> PasskeyCancelledException()
            "NotSupportedError", "SecurityError" -> UnsupportedPlatformException()
            else -> PasskeyRejectedException()
        }
    }
}

internal fun servedUnderRelyingParty(hostname: String): Boolean =
    hostname == RELYING_PARTY_DOMAIN || hostname.endsWith(".$RELYING_PARTY_DOMAIN") || hostname == "localhost"
