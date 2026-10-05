package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate

/**
 * Where a stock increase of a lot-tracked drug goes (pharmacy-api ADR-0007):
 * an existing lot, or a new lot with its number and expiry.
 */
sealed interface LotTarget {
    data class Existing(val lotId: String) : LotTarget
    data class New(val lotNumber: String, val expiryDate: LocalDate) : LotTarget
}
