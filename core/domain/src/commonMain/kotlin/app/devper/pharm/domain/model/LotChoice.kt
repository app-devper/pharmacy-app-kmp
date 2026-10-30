package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate

data class LotChoice(
    val lots: List<DrugLot>,
    val choice: String,
    val newLotNumber: String = "",
    val newLotExpiry: String = "",
) {
    val choosingNewLot: Boolean get() = choice == NEW_LOT

    val target: LotTarget?
        get() = when (choice) {
            "" -> null
            NEW_LOT -> {
                val expiry = runCatching { LocalDate.parse(newLotExpiry.trim()) }.getOrNull()
                if (newLotNumber.isBlank() || expiry == null) null else LotTarget.New(newLotNumber.trim(), expiry)
            }
            else -> LotTarget.Existing(choice)
        }

    fun choose(id: String): LotChoice = copy(choice = id)
    fun withNewLotNumber(value: String): LotChoice = copy(newLotNumber = value)
    fun withNewLotExpiry(value: String): LotChoice = copy(newLotExpiry = value)

    companion object {
        const val NEW_LOT = "new"

        fun forIncrease(lots: List<DrugLot>): LotChoice? {
            val usable = lots.filterNot { it.writtenOff }.sortedByDescending { it.expiryDate }
            return if (usable.isEmpty()) null else LotChoice(lots = usable, choice = usable.first().id)
        }
    }
}
