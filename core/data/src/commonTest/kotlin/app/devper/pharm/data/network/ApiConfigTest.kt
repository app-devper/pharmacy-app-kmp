package app.devper.pharm.data.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiConfigTest {

    @Test
    fun default_base_url_points_at_unified_devper_host() {
        val config = ApiConfig()
        assertTrue(config.apiBaseUrl.startsWith("https://"))
        assertEquals("https://api.devper.app", config.apiBaseUrl)
    }

    @Test
    fun um_endpoint_helpers_build_correct_paths() {
        val config = ApiConfig(apiBaseUrl = "http://localhost:8585")
        assertEquals("http://localhost:8585/api/um/v1/auth/login", config.umAuthLogin)
        assertEquals("http://localhost:8585/api/um/v1/auth/logout", config.umAuthLogout)
        assertEquals("http://localhost:8585/api/um/v1/user/info", config.umAuthInfo)
    }

    @Test
    fun pharmacy_path_with_leading_slash_is_preserved() {
        val config = ApiConfig(apiBaseUrl = "http://localhost:8087")
        assertEquals("http://localhost:8087/api/pharmacy/v1/drugs", config.pharmacy("/drugs"))
    }

    @Test
    fun pharmacy_path_without_leading_slash_gets_one_inserted() {
        val config = ApiConfig(apiBaseUrl = "http://localhost:8087")
        assertEquals("http://localhost:8087/api/pharmacy/v1/drugs", config.pharmacy("drugs"))
    }

    @Test
    fun um_user_path_helper_resolves_against_unified_base() {
        val config = ApiConfig(apiBaseUrl = "http://localhost:8585")
        assertEquals("http://localhost:8585/api/um/v1/user", config.umUser())
        assertEquals("http://localhost:8585/api/um/v1/user/42", config.umUser("/42"))
        assertEquals("http://localhost:8585/api/um/v1/user/42", config.umUser("42"))
    }

    @Test
    fun config_is_a_data_class_with_equality() {
        val a = ApiConfig(apiBaseUrl = "y")
        val b = ApiConfig(apiBaseUrl = "y")
        assertEquals(a, b)
    }
}
