package app.devper.pharm.domain.observer

import app.devper.pharm.common.NetworkException
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.repository.FakeProfileRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignedInUserTest {

    private class CountingProfile(private val inner: FakeProfileRepository) : app.devper.pharm.domain.repository.profile.ProfileRepository by inner {
        var gets = 0
        override suspend fun get() = inner.get().also { gets++ }
    }

    @Test
    fun get_fetches_once_and_then_answers_from_the_signed_in_user() = runTest {
        val profile = CountingProfile(FakeProfileRepository())
        val user = SignedInUser(profile)
        user.get()
        user.get()
        assertEquals(1, profile.gets)
        assertEquals("u-1", user.user.value?.id)
    }

    @Test
    fun an_unreadable_profile_means_no_role_and_no_financial_reports() = runTest {
        val user = SignedInUser(FakeProfileRepository(getFailsWith = NetworkException()))
        assertEquals(Role.UNKNOWN, user.role())
        assertFalse(user.permissions().canSeeFinancialReports)
        assertEquals("", user.displayNameOrBlank())
    }

    @Test
    fun an_admin_sees_financial_reports() = runTest {
        val user = SignedInUser(FakeProfileRepository(FakeProfileRepository.sampleUser.copy(role = Role.ADMIN)))
        assertTrue(user.permissions().canSeeFinancialReports)
    }

    @Test
    fun replace_shows_an_edited_profile_and_clear_forgets_it() = runTest {
        val user = SignedInUser(FakeProfileRepository())
        user.replace(FakeProfileRepository.sampleUser.copy(firstName = "สมหญิง"))
        assertEquals("สมหญิง", user.user.value?.firstName)
        user.clear()
        assertNull(user.user.value)
    }
}
