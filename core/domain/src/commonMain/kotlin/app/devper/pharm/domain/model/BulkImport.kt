package app.devper.pharm.domain.model

data class BulkImportResult(
    val imported: Int,
    val errors: List<BulkImportRowError>,
) {
    val totalAttempted: Int get() = imported + errors.size
    val hasErrors: Boolean get() = errors.isNotEmpty()
}

data class BulkImportRowError(
    val row: Int,
    val name: String,
    val message: String,
)
