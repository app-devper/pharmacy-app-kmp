package app.devper.pharm.ui.i18n

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.ServerException
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.common.ValidationException
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CommonErrorLocalizeTest {

    private val transport = listOf(
        AuthException(), ForbiddenException(), NotFoundException(), ConflictException(),
        NetworkException(), ServerException(), ValidationException(), StorageException(),
        UnsupportedPlatformException(),
    )

    private val commonErrors = listOf(
        CommonUiStateError.LoadFailed(), CommonUiStateError.SaveFailed(),
        CommonUiStateError.DeleteFailed(), CommonUiStateError.ExportFailed(),
    )

    @Test
    fun every_transport_exception_localizes_nonblank_in_both_languages() {
        transport.forEach { ex ->
            assertTrue(ex.localizeCommon(PharmStringsTh).isNotBlank(), "${ex::class.simpleName} Th")
            assertTrue(ex.localizeCommon(PharmStringsEn).isNotBlank(), "${ex::class.simpleName} En")
        }
    }

    @Test
    fun every_common_error_localizes_nonblank_in_both_languages() {
        commonErrors.forEach { e ->
            assertTrue(e.localizeCommon(PharmStringsTh).isNotBlank())
            assertNotEquals(e.localizeCommon(PharmStringsTh), e.localizeCommon(PharmStringsEn))
        }
    }

    @Test
    fun common_errors_map_to_distinct_strings() {
        val th = commonErrors.map { it.localizeCommon(PharmStringsTh) }
        assertEquals(th.size, th.toSet().size, "each common error must have a distinct message")
    }

    @Test
    fun transport_errors_localize_by_type_not_by_baked_in_message() {
        assertEquals(PharmStringsTh.commonErrorNetwork, NetworkException().localizeCommon(PharmStringsTh))
        assertEquals(PharmStringsTh.commonErrorAuth, AuthException().localizeCommon(PharmStringsTh))
        assertEquals(PharmStringsEn.commonErrorServer, ServerException().localizeCommon(PharmStringsEn))
    }

    @Test
    fun validation_exception_passes_its_domain_message_through() {
        val specific = ValidationException("กรุณาระบุเหตุผลการยกเลิก")
        assertEquals("กรุณาระบุเหตุผลการยกเลิก", specific.localizeCommon(PharmStringsTh))
        assertEquals("กรุณาระบุเหตุผลการยกเลิก", specific.localizeCommon(PharmStringsEn))
    }

    @Test
    fun common_messages_localize_in_both_languages() {
        listOf(CommonUiStateMessage.ExportEmpty, CommonUiStateMessage.Saved).forEach { m ->
            assertTrue(m.localize(PharmStringsTh).isNotBlank())
            assertNotEquals(m.localize(PharmStringsTh), m.localize(PharmStringsEn))
        }
    }

    @Test
    fun export_done_passes_path_through_as_message() {
        val msg = CommonUiStateMessage.ExportDone("/downloads/report.xlsx")
        assertEquals("/downloads/report.xlsx", msg.localize(PharmStringsTh))
        assertEquals("/downloads/report.xlsx", msg.localize(PharmStringsEn))
    }
}
