package app.devper.pharm.domain.cart

import app.devper.pharm.domain.extension.calculateKyRequired
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.SaleKyCapture
import app.devper.pharm.domain.model.capture

class KyCaptureSession {

    sealed interface Decision {
        data object Proceed : Decision
        data class NeedsCapture(val required: KyRequired) : Decision
    }

    data class Submission(
        val capture: SaleKyCapture?,
        val skippedByCashier: Boolean,
    )

    private var fields: KyCaptureFields? = null
    private var required: KyRequired? = null
    private var skippedByCashier: Boolean = false
    private var precapturedLines: List<CartLine>? = null

    val isPrecaptured: Boolean get() = precapturedLines != null

    fun cartChanged(lines: List<CartLine>): Boolean {
        val precaptured = precapturedLines ?: return false
        if (lines == precaptured) return false
        fields = null
        precapturedLines = null
        return true
    }

    fun decide(lines: List<CartLine>, skipAuto: Boolean): Decision {
        val needed = lines.calculateKyRequired()
        if (needed.isEmpty) return Decision.Proceed
        if (skipAuto) {
            settle()
            return Decision.Proceed
        }
        required = needed
        return if (fields != null) Decision.Proceed else Decision.NeedsCapture(needed)
    }

    fun precaptureNeeded(lines: List<CartLine>): KyRequired? =
        lines.calculateKyRequired().takeUnless { it.isEmpty }

    fun precapture(captured: KyCaptureFields, lines: List<CartLine>) {
        fields = captured
        precapturedLines = lines
    }

    fun capture(captured: KyCaptureFields) {
        fields = captured
    }

    fun skip() {
        fields = null
        required = null
        skippedByCashier = true
        precapturedLines = null
    }

    fun cancel() {
        settle()
        precapturedLines = null
    }

    fun submission(): Submission {
        val needed = required
        val captured = fields
        return Submission(
            capture = if (needed != null && captured != null) needed.capture(captured) else null,
            skippedByCashier = skippedByCashier,
        )
    }

    fun settle() {
        fields = null
        required = null
        skippedByCashier = false
    }
}
