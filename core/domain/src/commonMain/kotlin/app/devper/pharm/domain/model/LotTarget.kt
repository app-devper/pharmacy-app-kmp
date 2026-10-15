package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate

sealed interface LotTarget {
    data class Existing(val lotId: String) : LotTarget
    data class New(val lotNumber: String, val expiryDate: LocalDate) : LotTarget
}
