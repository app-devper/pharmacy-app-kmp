package app.devper.pharm.data.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TokenStorageTest {

    @Test
    fun initially_no_token_when_secure_storage_is_empty() {
        val storage = TokenStorage(InMemorySecureStorage())
        assertNull(storage.token)
        assertNull(storage.tokenFlow.value)
    }

    @Test
    fun save_writes_value_and_updates_flow() {
        val storage = TokenStorage(InMemorySecureStorage())
        storage.save("jwt-1")
        assertEquals("jwt-1", storage.token)
        assertEquals("jwt-1", storage.tokenFlow.value)
    }

    @Test
    fun clear_removes_value_and_emits_null() {
        val storage = TokenStorage(InMemorySecureStorage())
        storage.save("jwt-1")
        storage.clear()
        assertNull(storage.token)
        assertNull(storage.tokenFlow.value)
    }

    @Test
    fun rehydrates_existing_value_on_construction() {
        val secure = InMemorySecureStorage().apply { put("auth.token", "preset") }
        val storage = TokenStorage(secure)
        assertEquals("preset", storage.token)
    }
}
