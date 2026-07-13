package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Drug
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

const val EXPIRY_WARNING_DAYS = 90

fun Drug.nextLotDaysLeft(today: LocalDate): Int? = nextLotExpiry?.let { today.daysUntil(it) }
