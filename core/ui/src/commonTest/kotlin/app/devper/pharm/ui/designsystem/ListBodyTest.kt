package app.devper.pharm.ui.designsystem

import app.devper.pharm.common.NetworkException
import kotlin.test.Test
import kotlin.test.assertEquals

class ListBodyTest {

    private val failure = NetworkException()

    @Test
    fun a_page_with_rows_always_shows_them_whatever_is_loading_or_failing() {
        assertEquals(ListBody.Content, listBodyOf(loading = true, error = failure, isEmpty = false))
        assertEquals(ListBody.Content, listBodyOf(loading = false, error = null, isEmpty = false))
    }

    @Test
    fun an_empty_page_shows_loading_before_a_failure_and_a_failure_before_empty() {
        assertEquals(ListBody.Loading, listBodyOf(loading = true, error = failure, isEmpty = true))
        assertEquals(ListBody.Loading, listBodyOf(loading = true, error = null, isEmpty = true))
        assertEquals(ListBody.Failed, listBodyOf(loading = false, error = failure, isEmpty = true))
        assertEquals(ListBody.Empty, listBodyOf(loading = false, error = null, isEmpty = true))
    }
}
