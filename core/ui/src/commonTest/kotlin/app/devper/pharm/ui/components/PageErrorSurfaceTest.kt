package app.devper.pharm.ui.components

import app.devper.pharm.common.NetworkException
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class PageErrorSurfaceTest {

    private val error = NetworkException()

    @Test
    fun no_error_stays_null_whatever_the_page_shows() {
        assertNull(null.unlessPageShowsError(pageIsEmpty = true))
        assertNull(null.unlessPageShowsError(pageIsEmpty = false))
    }

    @Test
    fun empty_page_renders_the_error_itself_so_the_sheet_stays_quiet() {
        assertNull(error.unlessPageShowsError(pageIsEmpty = true))
    }

    @Test
    fun page_with_rows_cannot_show_the_error_so_the_sheet_carries_it() {
        assertSame(error, error.unlessPageShowsError(pageIsEmpty = false))
    }
}
