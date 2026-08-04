package app.devper.pharm.ui.common

import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.ui.i18n.PharmStringsTh
import kotlin.test.Test
import kotlin.test.assertIs

class CommonMessageToastTest {

    @Test
    fun finishedWorkReadsAsSuccess() {
        assertIs<PharmToast.Success>(CommonUiStateMessage.Saved.toToast(PharmStringsTh))
        assertIs<PharmToast.Success>(CommonUiStateMessage.ExportDone("/tmp/a.csv").toToast(PharmStringsTh))
    }

    @Test
    fun anExportWithNothingInItIsNotReportedAsSuccess() {
        assertIs<PharmToast.Warning>(CommonUiStateMessage.ExportEmpty.toToast(PharmStringsTh))
    }
}
