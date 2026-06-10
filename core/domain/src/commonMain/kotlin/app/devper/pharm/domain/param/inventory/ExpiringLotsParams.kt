package app.devper.pharm.domain.param.inventory

data class ExpiringLotsFilterParam(
    val daysAhead: Int? = null,
    val expiredOnly: Boolean = false,
)

data class WriteoffLotsParam(
    val lotIds: List<String>,
)
