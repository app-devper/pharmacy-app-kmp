package app.devper.pharm.domain.extension

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
}
