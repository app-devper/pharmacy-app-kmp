package app.devper.pharm.domain.param.inventory

data class ReorderSuggestionsParam(
    val days: Int? = null,
    val lookahead: Int? = null,
)
