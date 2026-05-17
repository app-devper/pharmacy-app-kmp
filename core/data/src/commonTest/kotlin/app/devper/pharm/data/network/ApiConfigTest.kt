package app.devper.pharm.data.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiConfigTest {

    @Test
    fun default_um_base_url_points_at_cloud_run() {
        val config = ApiConfig()
        assertTrue(config.umBaseUrl.startsWith("https://"))
        assertTrue(config.umBaseUrl.contains("um"))
    }

    @Test
    fun default_pharmacy_url_points_at_cloud_run() {
        val config = ApiConfig()
        assertTrue(config.apiBaseUrl.startsWith("https://"))
        assertTrue(config.apiBaseUrl.contains("pharmacy"))
    }

    @Test
    fun um_endpoint_helpers_build_correct_paths() {
        val config = ApiConfig(umBaseUrl = "http://localhost:8585")
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
    fun config_is_a_data_class_with_equality() {
        val a = ApiConfig(umBaseUrl = "x", apiBaseUrl = "y")
        val b = ApiConfig(umBaseUrl = "x", apiBaseUrl = "y")
        assertEquals(a, b)
    }
}
