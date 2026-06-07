package app.devper.pharm.domain.model

data class BarcodeMatch(
    val drug: Drug,
    val altUnit: AltUnit?,
)
