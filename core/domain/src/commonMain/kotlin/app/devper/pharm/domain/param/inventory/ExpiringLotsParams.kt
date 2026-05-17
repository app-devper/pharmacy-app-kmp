package app.devper.pharm.domain.param

data class ExpiringLotsFilterParam(
    val daysAhead: Int? = null,
    val expiredOnly: Boolean = false,
)

data class WriteoffLotsParam(
    val lotIds: List<String>,
)
