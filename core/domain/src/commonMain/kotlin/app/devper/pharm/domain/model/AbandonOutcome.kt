package app.devper.pharm.domain.model

sealed interface AbandonOutcome {
    data object Abandoned : AbandonOutcome

    data class AlreadyRecorded(val sale: Sale) : AbandonOutcome
}
