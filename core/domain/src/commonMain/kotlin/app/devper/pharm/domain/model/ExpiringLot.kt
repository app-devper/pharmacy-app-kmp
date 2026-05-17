package app.devper.pharm.domain.model

data class ExpiringLot(
    val id: String,
    val drugId: String,
    val drugName: String,
    val lotNumber: String,
    val expiryDate: String,
    val remaining: Int,
    val daysLeft: Int,
)

data class WriteoffResult(
    val writtenOff: Int,
    val failures: List<WriteoffFailure>,
) {
    val hasFailures: Boolean get() = failures.isNotEmpty()
    val totalAttempted: Int get() = writtenOff + failures.size
}

data class WriteoffFailure(
    val lotId: String,
    val message: String,
)
