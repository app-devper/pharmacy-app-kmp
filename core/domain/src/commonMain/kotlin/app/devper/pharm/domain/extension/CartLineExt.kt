package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.KyRequired

fun List<CartLine>.calculateKyRequired(): KyRequired {
    val ky10 = mutableListOf<CartLine>()
    val ky11 = mutableListOf<CartLine>()
    val ky12 = mutableListOf<CartLine>()
    for (line in this) {
        for (type in line.drug.reportTypes) {
            when (type.lowercase()) {
                "ky10" -> ky10 += line
                "ky11" -> ky11 += line
                "ky12" -> ky12 += line
            }
        }
    }
    return KyRequired(ky10 = ky10, ky11 = ky11, ky12 = ky12)
}
