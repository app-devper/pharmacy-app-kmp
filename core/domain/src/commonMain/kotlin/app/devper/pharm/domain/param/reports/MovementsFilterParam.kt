package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.MovementType

data class MovementsFilterParam(
    val from: String? = null,
    val to: String? = null,
    val drugName: String? = null,
    val types: Set<MovementType> = emptySet(),
    val limit: Int = 100,
    val offset: Int = 0,
)
