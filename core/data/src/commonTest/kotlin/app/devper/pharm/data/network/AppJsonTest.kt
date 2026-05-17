package app.devper.pharm.data.network

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Serializable
private data class SampleDto(
    val id: String,
    val name: String,
    val nickname: String? = null,
    val count: Int = 0,
)

class AppJsonTest {

    @Test
    fun ignores_unknown_keys_in_input() {
        val raw = """{"id":"1","name":"foo","extraField":"surprise"}"""
        val parsed = AppJson.decodeFromString(SampleDto.serializer(), raw)
        assertEquals("1", parsed.id)
        assertEquals("foo", parsed.name)
    }

    @Test
    fun does_not_encode_default_values() {
        val obj = SampleDto(id = "1", name = "foo")
        val output = AppJson.encodeToString(SampleDto.serializer(), obj)
        assertFalse(output.contains("nickname"))
        assertFalse(output.contains("count"))
        assertTrue(output.contains("\"id\":\"1\""))
        assertTrue(output.contains("\"name\":\"foo\""))
    }

    @Test
    fun null_explicit_field_is_treated_as_absent_on_decode() {
        val raw = """{"id":"1","name":"foo","nickname":null}"""
        val parsed = AppJson.decodeFromString(SampleDto.serializer(), raw)
        assertEquals(null, parsed.nickname)
    }
}
