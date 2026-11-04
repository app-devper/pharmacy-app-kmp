package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals

class LinePriceTest {

    private fun drug(sellPrice: Money = Money(10.0)) = Drug(
        id = "d1",
        name = "Amoxicillin",
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = sellPrice,
        costPrice = Money(0.0),
        stock = Quantity(100),
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = null,
    )

    @Test
    fun packListedAt100WithFactor3_isPricedAt99_99_asTheServerRecordsIt() {
        val pack = AltUnit(name = "แพ็ค", factor = 3, sellPrice = Money(100.0))

        assertEquals(Money(33.33), basePriceOf(drug(), pack, Tier.Retail))
        assertEquals(Money(99.99), linePrice(drug(), pack, Tier.Retail))
    }

    @Test
    fun baseUnit_isTheResolvedDrugPrice() {
        assertEquals(Money(10.0), linePrice(drug(sellPrice = Money(10.0)), null, Tier.Retail))
    }

    @Test
    fun altUnitTierPriceOfZero_fallsBackToRetailThenBase() {
        val pack = AltUnit(
            name = "กล่อง", factor = 10, sellPrice = Money(90.0),
            prices = mapOf("wholesale" to Money(0.0), "retail" to Money(100.0)),
        )

        assertEquals(Money(100.0), linePrice(drug(), pack, Tier.Wholesale))
    }

    @Test
    fun altUnitWithEveryTierPriceZero_fallsBackToTheUnitSellPrice() {
        val pack = AltUnit(
            name = "กล่อง", factor = 10, sellPrice = Money(90.0),
            prices = mapOf("wholesale" to Money(0.0), "retail" to Money(0.0)),
        )

        assertEquals(Money(90.0), linePrice(drug(), pack, Tier.Wholesale))
    }

    @Test
    fun boxOf12At250_linePriceIsExactly249_96() {
        val box = AltUnit("กล่อง", 12, Money(250.0))

        assertEquals(Money(249.96), linePrice(drug(), box, Tier.Retail))
        assertEquals("249.96", linePrice(drug(), box, Tier.Retail).format())
    }
}
