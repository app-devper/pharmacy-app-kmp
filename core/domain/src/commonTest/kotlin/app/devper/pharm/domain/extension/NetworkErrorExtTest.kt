package app.devper.pharm.domain.extension

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.IdentityUnavailableException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.ServerException
import app.devper.pharm.common.ValidationException
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class ConnectException(message: String) : RuntimeException(message)
private class ConnectTimeoutException(message: String) : RuntimeException(message)
private class SocketTimeoutException(message: String) : RuntimeException(message)
private class SocketException(message: String) : RuntimeException(message)
private class UnknownHostException(message: String) : RuntimeException(message)
private class EOFException(message: String) : RuntimeException(message)
private class IOException(message: String) : RuntimeException(message)

class NetworkErrorExtTest {

    @Test
    fun identity_outage_is_a_temporary_outage_but_not_a_network_error() {
        assertTrue(IdentityUnavailableException().looksLikeTemporaryOutage())
        assertFalse(IdentityUnavailableException().looksLikeNetworkError())
    }

    @Test
    fun network_error_is_a_temporary_outage() {
        assertTrue(ConnectException("any").looksLikeTemporaryOutage())
        assertFalse(IllegalStateException("validation error").looksLikeTemporaryOutage())
    }

    @Test
    fun connect_exception_class_name_is_network() {
        assertTrue(ConnectException("any").looksLikeNetworkError())
    }

    @Test
    fun connect_timeout_class_name_is_network() {
        assertTrue(ConnectTimeoutException("timed out").looksLikeNetworkError())
    }

    @Test
    fun socket_timeout_class_name_is_network() {
        assertTrue(SocketTimeoutException("timed out").looksLikeNetworkError())
    }

    @Test
    fun socket_exception_class_name_is_network() {
        assertTrue(SocketException("Connection reset by peer").looksLikeNetworkError())
    }

    @Test
    fun socket_exception_with_generic_message_still_network() {
        assertTrue(SocketException("any opaque message").looksLikeNetworkError())
    }

    @Test
    fun unknown_host_class_name_is_network() {
        assertTrue(UnknownHostException("api.example.com").looksLikeNetworkError())
    }

    @Test
    fun eof_exception_from_broken_pipe_is_network() {
        assertTrue(EOFException("broken pipe").looksLikeNetworkError())
    }

    @Test
    fun io_exception_is_network() {
        assertTrue(IOException("any").looksLikeNetworkError())
    }

    @Test
    fun network_unreachable_message_is_network() {
        assertTrue(RuntimeException("Network is unreachable").looksLikeNetworkError())
    }

    @Test
    fun connection_refused_message_is_network() {
        assertTrue(RuntimeException("Connection refused").looksLikeNetworkError())
    }

    @Test
    fun failed_to_fetch_from_browser_is_network() {
        assertTrue(RuntimeException("Failed to fetch").looksLikeNetworkError())
    }

    @Test
    fun could_not_resolve_dns_message_is_network() {
        assertTrue(RuntimeException("Could not resolve host: api.example.com").looksLikeNetworkError())
    }

    @Test
    fun typed_AppException_in_cause_chain_is_detected() {
        val cause = ConnectException("dropped")
        val wrapped = RuntimeException("checkout failed", cause)
        assertTrue(wrapped.looksLikeNetworkError())
    }

    @Test
    fun validation_exception_is_not_network() {
        assertFalse(IllegalArgumentException("invalid input").looksLikeNetworkError())
    }

    @Test
    fun arbitrary_runtime_exception_is_not_network() {
        assertFalse(RuntimeException("some app bug").looksLikeNetworkError())
    }

    @Test
    fun null_message_does_not_blow_up() {
        assertFalse(RuntimeException(null as String?).looksLikeNetworkError())
    }

    @Test
    fun delivery_failure_rule_reads_the_transport_types() {
        val temporary = listOf(
            NetworkException(cause = RuntimeException("opaque")),
            NetworkException(),
            IdentityUnavailableException(),
            AuthException(),
            ForbiddenException(),
            ServerException(statusCode = 500),
            ServerException(statusCode = 502),
            ServerException(statusCode = 503),
            ServerException(),
            ConnectException("any"),
        )
        val refused = listOf(
            ServerException(statusCode = 400),
            ServerException(statusCode = 422),
            ConflictException(),
            NotFoundException(),
            ValidationException(),
            IllegalStateException("validation error"),
            RuntimeException("Failed to connect to host").let { ConflictException(cause = it) },
        )
        temporary.forEach { assertTrue(it.isTemporaryDeliveryFailure(), "$it should be kept and retried") }
        refused.forEach { assertFalse(it.isTemporaryDeliveryFailure(), "$it is the server's answer") }
    }
}
