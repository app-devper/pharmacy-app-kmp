package app.devper.pharm.presentation.stock

import app.devper.pharm.presentation.stock.exception.DrugFormUiStateError

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.param.CreateLotPayload
import app.devper.pharm.domain.param.UpdateDrugParam
import app.devper.pharm.domain.usecase.inventory.AddDrugUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.UpdateDrugUseCase
import app.devper.pharm.ui.common.BaseFormViewModel
import app.devper.pharm.ui.format.toLocalDateOrNull

class DrugFormViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val addDrug: AddDrugUseCase,
    private val updateDrug: UpdateDrugUseCase,
) : BaseFormViewModel<DrugFormUiState>(DrugFormUiState()) {

    fun init(mode: DrugFormMode) {
        setState { copy(mode = mode) }
        if (mode is DrugFormMode.Edit) hydrateForEdit(mode.drugId)
    }

    fun onName(v: String) = patch { copy(name = v) }
    fun onGenericName(v: String) = patch { copy(genericName = v) }
    fun onType(v: String) = patch { copy(type = v) }
    fun onStrength(v: String) = patch { copy(strength = v) }
    fun onBarcode(v: String) = patch { copy(barcode = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onSellPrice(v: String) = patch { copy(sellPrice = v.numericOnly()) }
    fun onCostPrice(v: String) = patch { copy(costPrice = v.numericOnly()) }
    fun onMinStock(v: String) = patch { copy(minStock = v.intOnly()) }
    fun onTierRetail(v: String) = patch { copy(tierRetail = v.numericOnly()) }
    fun onTierRegular(v: String) = patch { copy(tierRegular = v.numericOnly()) }
    fun onTierWholesale(v: String) = patch { copy(tierWholesale = v.numericOnly()) }
    fun onInitialStock(v: String) = patch { copy(initialStock = v.intOnly()) }
    fun onLotNumber(v: String) = patch { copy(lotNumber = v) }
    fun onLotExpiry(v: String) = patch { copy(lotExpiry = v) }
    fun onLotQty(v: String) = patch { copy(lotQty = v.intOnly()) }
    fun onLotCostPrice(v: String) = patch { copy(lotCostPrice = v.numericOnly()) }
    fun onLotSellPrice(v: String) = patch { copy(lotSellPrice = v.numericOnly()) }

    fun onToggleReportType(type: String) = patch {
        val next = if (type in reportTypes) reportTypes - type else reportTypes + type
        copy(reportTypes = next)
    }

    fun onAddAltUnit() = patch { copy(altUnits = altUnits + AltUnitDraft()) }
    fun onRemoveAltUnit(index: Int) = patch {
        copy(altUnits = altUnits.toMutableList().also { if (index in it.indices) it.removeAt(index) })
    }
    fun onAltUnitName(index: Int, v: String) = updateAltUnit(index) { copy(name = v) }
    fun onAltUnitFactor(index: Int, v: String) = updateAltUnit(index) { copy(factor = v.intOnly()) }
    fun onAltUnitSellPrice(index: Int, v: String) = updateAltUnit(index) { copy(sellPrice = v.numericOnly()) }
    fun onAltUnitBarcode(index: Int, v: String) = updateAltUnit(index) { copy(barcode = v) }
    fun onAltUnitHidden(index: Int, v: Boolean) = updateAltUnit(index) { copy(hidden = v) }

    override suspend fun persist(): Result<Unit> {
        val s = current
        return when (val mode = s.mode) {
            is DrugFormMode.Add  -> addDrug(buildAddParam(s.form)).map { Unit }
            is DrugFormMode.Edit -> updateDrug(buildUpdateParam(s.form, mode.drugId))
        }
    }

    private fun hydrateForEdit(drugId: String) {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list ->
                val drug = list.firstOrNull { it.id == drugId }
                if (drug == null) {
                    setState { copy(loading = false, errorState = DrugFormUiStateError.NotFound()) }
                } else {
                    setState { copy(loading = false, form = drug.toForm()) }
                }
            },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }

    private fun Drug.toForm(): DrugFormFields = DrugFormFields(
        name = name,
        genericName = genericName.orEmpty(),
        type = type.orEmpty(),
        strength = strength.orEmpty(),
        barcode = barcode.orEmpty(),
        regNo = regNo.orEmpty(),
        unit = unit ?: "ชิ้น",
        sellPrice = if (sellPrice.isZero) "" else sellPrice.amount.toPlain(),
        costPrice = if (costPrice.isZero) "" else costPrice.amount.toPlain(),
        minStock = if (minStock.isZero) "" else minStock.value.toString(),
        tierRetail = prices["retail"]?.amount?.toPlain().orEmpty(),
        tierRegular = prices["regular"]?.amount?.toPlain().orEmpty(),
        tierWholesale = prices["wholesale"]?.amount?.toPlain().orEmpty(),
        altUnits = altUnits.map {
            AltUnitDraft(
                name = it.name,
                factor = it.factor.toString(),
                sellPrice = if (it.sellPrice.isZero) "" else it.sellPrice.amount.toPlain(),
                barcode = it.barcode.orEmpty(),
                hidden = it.hidden,
            )
        },
        reportTypes = reportTypes.toSet(),
    )

    private fun buildAddParam(f: DrugFormFields): AddDrugParam {
        val stock = f.initialStock.toIntOrNull() ?: 0
        val parsedLotExpiry = f.lotExpiry.trim().toLocalDateOrNull()
        if (stock > 0 && parsedLotExpiry == null) {
            throw DrugFormUiStateError.InvalidInitialLotExpiry()
        }
        val createLot = if (stock > 0 && parsedLotExpiry != null) {
            CreateLotPayload(
                lotNumber = f.lotNumber.trim(),
                expiryDate = parsedLotExpiry,
                importDate = null,
                costPrice = f.lotCostPrice.toDoubleOrNull()?.let(::Money),
                sellPrice = f.lotSellPrice.toDoubleOrNull()?.let(::Money),
                quantity = Quantity(f.lotQty.toIntOrNull() ?: stock),
            )
        } else null

        return AddDrugParam(
            name = f.name.trim(),
            genericName = f.genericName.trim(),
            type = f.type.trim(),
            strength = f.strength.trim(),
            barcode = f.barcode.trim(),
            sellPrice = Money(f.sellPrice.toDoubleOrNull() ?: 0.0),
            costPrice = Money(f.costPrice.toDoubleOrNull() ?: 0.0),
            stock = Quantity(stock),
            minStock = Quantity(f.minStock.toIntOrNull() ?: 0),
            regNo = f.regNo.trim(),
            unit = f.unit.trim().ifBlank { "ชิ้น" },
            reportTypes = f.reportTypes.toList(),
            altUnits = f.altUnits.map { it.toDomain() },
            prices = buildPriceMap(f),
            createLot = createLot,
        )
    }

    private fun buildUpdateParam(f: DrugFormFields, id: String): UpdateDrugParam =
        UpdateDrugParam(
            id = id,
            name = f.name.trim(),
            genericName = f.genericName.trim(),
            type = f.type.trim(),
            strength = f.strength.trim(),
            barcode = f.barcode.trim(),
            sellPrice = Money(f.sellPrice.toDoubleOrNull() ?: 0.0),
            costPrice = Money(f.costPrice.toDoubleOrNull() ?: 0.0),
            minStock = Quantity(f.minStock.toIntOrNull() ?: 0),
            regNo = f.regNo.trim(),
            unit = f.unit.trim().ifBlank { "ชิ้น" },
            reportTypes = f.reportTypes.toList(),
            altUnits = f.altUnits.map { it.toDomain() },
            prices = buildPriceMap(f),
        )

    private fun buildPriceMap(f: DrugFormFields): Map<String, Money> = buildMap {
        f.tierRetail.toDoubleOrNull()?.takeIf { it > 0 }?.let { put("retail", Money(it)) }
        f.tierRegular.toDoubleOrNull()?.takeIf { it > 0 }?.let { put("regular", Money(it)) }
        f.tierWholesale.toDoubleOrNull()?.takeIf { it > 0 }?.let { put("wholesale", Money(it)) }
    }

    private fun AltUnitDraft.toDomain(): AltUnit = AltUnit(
        name = name.trim(),
        factor = factor.toIntOrNull() ?: 0,
        sellPrice = Money(sellPrice.toDoubleOrNull() ?: 0.0),
        prices = emptyMap(),
        barcode = barcode.takeIf { it.isNotBlank() }?.trim(),
        hidden = hidden,
    )

    private fun patch(transform: DrugFormFields.() -> DrugFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun updateAltUnit(index: Int, transform: AltUnitDraft.() -> AltUnitDraft) {
        patch {
            copy(
                altUnits = altUnits.toMutableList().also {
                    if (index in it.indices) it[index] = it[index].transform()
                },
            )
        }
    }
}

private fun String.numericOnly(): String {
    var seenDot = false
    val sb = StringBuilder(length)
    for (c in this) when {
        c.isDigit()           -> sb.append(c)
        c == '.' && !seenDot  -> { sb.append('.'); seenDot = true }
        else                  -> {}
    }
    return sb.toString()
}

private fun String.intOnly(): String = filter { it.isDigit() }

private fun Double.toPlain(): String {
    val asLong = toLong()
    return if (asLong.toDouble() == this) asLong.toString() else this.toString()
}
