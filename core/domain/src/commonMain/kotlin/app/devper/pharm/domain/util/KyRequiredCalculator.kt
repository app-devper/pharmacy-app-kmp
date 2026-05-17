package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.KyRequired

object KyRequiredCalculator {

    fun calculate(cart: List<CartLine>): KyRequired {
        val ky10 = mutableListOf<CartLine>()
        val ky11 = mutableListOf<CartLine>()
        val ky12 = mutableListOf<CartLine>()
        for (line in cart) {
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
}
