package app.devper.pharm.domain.model

/** What the server did with a request to abandon a queued sale (pharmacy-api ADR-0009). */
sealed interface AbandonOutcome {
    /** Recorded as abandoned; the sale will never be recorded. */
    data object Abandoned : AbandonOutcome

    /** The sale was recorded before it could be abandoned. */
    data class AlreadyRecorded(val sale: Sale) : AbandonOutcome
}
