package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug

object BarcodeMatcher {

    data class Match(val drug: Drug, val altUnit: AltUnit?)

    fun match(drugs: List<Drug>, code: String): Match? {
        val needle = code.trim()
        if (needle.isEmpty()) return null

        for (drug in drugs) {
            val alt = drug.altUnits.firstOrNull { !it.hidden && it.barcode == needle }
            if (alt != null) return Match(drug, alt)
        }

        drugs.firstOrNull { it.barcode == needle }?.let { return Match(it, null) }

        drugs.firstOrNull { it.regNo == needle }?.let { return Match(it, null) }
        return null
    }
}
