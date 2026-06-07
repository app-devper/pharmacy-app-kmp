package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.MovementType
import kotlinx.datetime.LocalDate

data class MovementsFilterParam(
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val drugName: String? = null,
    val types: Set<MovementType> = emptySet(),
    val limit: Int = 100,
    val offset: Int = 0,
)
