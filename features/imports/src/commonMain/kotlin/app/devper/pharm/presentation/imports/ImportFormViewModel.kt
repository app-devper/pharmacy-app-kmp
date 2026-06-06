package app.devper.pharm.presentation.imports

import app.devper.pharm.common.ValidationException
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.param.AddPurchaseOrderParam
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam
import app.devper.pharm.domain.extension.buildPurchaseOrderItemInput
import app.devper.pharm.domain.extension.isPurchaseOrderLineValid
import app.devper.pharm.domain.usecase.AddPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.UpdatePurchaseOrderUseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class ImportFormViewModel(
    private val getPurchaseOrder: GetPurchaseOrderUseCase,
    private val addPurchaseOrder: AddPurchaseOrderUseCase,
    private val updatePurchaseOrder: UpdatePurchaseOrderUseCase,
    private val getDrugs: GetDrugsUseCase,
    private val getSuppliers: GetSuppliersUseCase,
) : BaseFormViewModel<ImportFormUiState>(ImportFormUiState()) {

    fun init(mode: ImportFormMode) {
        setState { copy(mode = mode) }
        loadDrugs()
        loadSuppliers()
        if (mode is ImportFormMode.Edit) hydrateForEdit(mode.importId)
    }

    fun onSupplier(v: String) = patchHeader { copy(supplier = v) }
    fun onInvoiceNo(v: String) = patchHeader { copy(invoiceNo = v) }
    fun onReceiveDate(v: String) = patchHeader { copy(receiveDate = v) }
    fun onNotes(v: String) = patchHeader { copy(notes = v) }

    fun addLine() = patchHeader { copy(items = items + ImportLineFields()) }
    fun removeLine(index: Int) = patchHeader {
        copy(items = items.filterIndexed { i, _ -> i != index })
    }

    fun onLineDrug(index: Int, drug: Drug) = patchLine(index) {
        copy(
            drugId = drug.id,
            drugName = drug.name,
            sellPrice = if (sellPrice.isBlank()) drug.sellPrice.cleanPrice() else sellPrice,
            costPrice = if (costPrice.isBlank()) drug.costPrice.cleanPrice() else costPrice,
        )
    }

    fun onLineLotNumber(index: Int, v: String) = patchLine(index) { copy(lotNumber = v) }
    fun onLineExpiry(index: Int, v: String) = patchLine(index) { copy(expiryDate = v) }
    fun onLineQty(index: Int, v: String) = patchLine(index) { copy(qty = v.filter { it.isDigit() }) }
    fun onLineCost(index: Int, v: String) = patchLine(index) { copy(costPrice = v.filterMoney()) }
    fun onLineSell(index: Int, v: String) = patchLine(index) { copy(sellPrice = v.filterMoney()) }

    override suspend fun persist(): Result<Unit> {
        val f = current.form

        val itemInputs = f.items.map { line ->
            buildPurchaseOrderItemInput(
                drugId = line.drugId,
                drugName = line.drugName,
                lotNumber = line.lotNumber,
                expiryDate = line.expiryDate,
                qty = line.qty,
                costPrice = line.costPrice,
                sellPrice = line.sellPrice,
            ).getOrElse { e ->
                return Result.failure(ValidationException(message = "ตรวจสอบข้อมูลไม่ผ่าน: ${e.message}", cause = e))
            }
        }
        return when (val mode = current.mode) {
            is ImportFormMode.Add -> addPurchaseOrder(
                AddPurchaseOrderParam(
                    supplier = f.supplier.trim(),
                    invoiceNo = f.invoiceNo.trim(),
                    receiveDate = f.receiveDate.trim(),
                    notes = f.notes.trim(),
                    items = itemInputs,
                ),
            ).map { Unit }

            is ImportFormMode.Edit -> updatePurchaseOrder(
                UpdatePurchaseOrderParam(
                    id = mode.importId,
                    supplier = f.supplier.trim(),
                    invoiceNo = f.invoiceNo.trim(),
                    receiveDate = f.receiveDate.trim(),
                    notes = f.notes.trim(),
                    items = itemInputs,
                ),
            ).map { Unit }
        }
    }

    private fun loadDrugs() {
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(drugs = list) } },
            onFailure = { e -> setState { copy(error = e.message ?: "โหลดข้อมูลยาไม่สำเร็จ") } },
        )
    }

    private fun loadSuppliers() {
        launchResult(
            block = { getSuppliers() },
            onSuccess = { list -> setState { copy(suppliers = list) } },
            onFailure = { e -> setState { copy(error = e.message ?: "โหลดข้อมูลซัพพลายเออร์ไม่สำเร็จ") } },
        )
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getPurchaseOrder(id) },
            onSuccess = { po ->
                val readOnly = po.status == PurchaseOrderStatus.Confirmed
                setState {
                    copy(
                        loading = false,
                        readOnly = readOnly,
                        form = ImportFormFields(
                            supplier = po.supplier,
                            invoiceNo = po.invoiceNo,
                            receiveDate = po.receiveDate.take(10),
                            notes = po.notes,
                            items = po.items.map { item ->
                                ImportLineFields(
                                    drugId = item.drugId,
                                    drugName = item.drugName,
                                    lotNumber = item.lotNumber,
                                    expiryDate = item.expiryDate,
                                    qty = item.qty.toString(),
                                    costPrice = item.costPrice.cleanPrice(),
                                    sellPrice = item.sellPrice?.cleanPrice() ?: "",
                                )
                            },
                        ),
                    )
                }
            },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }

    private fun patchHeader(transform: ImportFormFields.() -> ImportFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun patchLine(index: Int, transform: ImportLineFields.() -> ImportLineFields) {
        setState {
            val items = form.items
            if (index !in items.indices) return@setState this
            val patched = items.toMutableList().also { it[index] = it[index].transform() }
            copy(form = form.copy(items = patched))
        }
    }
}

private fun Double.cleanPrice(): String =
    if (this % 1.0 == 0.0) this.toLong().toString() else this.toString()

private fun String.filterMoney(): String {
    val filtered = filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) filtered
    else filtered.substring(0, firstDot + 1) +
        filtered.substring(firstDot + 1).filter { it != '.' }
}
