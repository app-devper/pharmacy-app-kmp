package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug

data class BarcodeMatch(val drug: Drug, val altUnit: AltUnit?)

fun List<Drug>.matchBarcode(code: String): BarcodeMatch? {
    val needle = code.trim()
    if (needle.isEmpty()) return null

    for (drug in this) {
        val alt = drug.altUnits.firstOrNull { !it.hidden && it.barcode == needle }
        if (alt != null) return BarcodeMatch(drug, alt)
    }

    firstOrNull { it.barcode == needle }?.let { return BarcodeMatch(it, null) }
    firstOrNull { it.regNo == needle }?.let { return BarcodeMatch(it, null) }
    return null
}
