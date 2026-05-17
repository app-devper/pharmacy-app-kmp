package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.KyApi
import app.devper.pharm.data.remote.dto.Ky10Dto
import app.devper.pharm.data.remote.dto.Ky10Request
import app.devper.pharm.data.remote.dto.Ky11Dto
import app.devper.pharm.data.remote.dto.Ky11Request
import app.devper.pharm.data.remote.dto.Ky12Dto
import app.devper.pharm.data.remote.dto.Ky12Request
import app.devper.pharm.data.remote.dto.Ky9Dto
import app.devper.pharm.data.remote.dto.Ky9Request
import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.param.AddKy9Param
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.repository.KyRepository

class KyRepositoryImpl(private val api: KyApi) : KyRepository {

    override suspend fun submitKy10(form: KyForm.Ky10) {
        api.submitKy10(
            Ky10Request(
                saleId = form.saleId,
                date = form.date,
                drugName = form.drugName,
                regNo = form.regNo,
                qty = form.qty,
                unit = form.unit,
                buyerName = form.buyerName,
                buyerAddress = form.buyerAddress,
                rxNo = form.rxNo,
                doctor = form.doctor,
                balance = form.balance,
            ),
        )
    }

    override suspend fun submitKy11(form: KyForm.Ky11) {
        api.submitKy11(
            Ky11Request(
                saleId = form.saleId,
                date = form.date,
                drugName = form.drugName,
                regNo = form.regNo,
                qty = form.qty,
                unit = form.unit,
                buyerName = form.buyerName,
                purpose = form.purpose,
                pharmacist = form.pharmacist,
            ),
        )
    }

    override suspend fun submitKy12(form: KyForm.Ky12) {
        api.submitKy12(
            Ky12Request(
                saleId = form.saleId,
                date = form.date,
                rxNo = form.rxNo,
                patientName = form.patientName,
                doctor = form.doctor,
                hospital = form.hospital,
                drugName = form.drugName,
                qty = form.qty,
                unit = form.unit,
                totalValue = form.totalValue,
                status = form.status,
            ),
        )
    }

    override suspend fun addKy9(param: AddKy9Param) {
        api.addKy9(
            Ky9Request(
                date = param.date.trim(),
                drugName = param.drugName.trim(),
                regNo = param.regNo.trim(),
                unit = param.unit.trim(),
                qty = param.qty,
                pricePerUnit = param.pricePerUnit,
                seller = param.seller.trim(),
                invoiceNo = param.invoiceNo.trim(),
            ),
        )
    }

    override suspend fun listKy9(filter: KyMonthFilterParam): List<Ky9Entry> =
        api.listKy9(filter.month.trim()).map(::toDomain)

    override suspend fun listKy10(filter: KyMonthFilterParam): List<Ky10Entry> =
        api.listKy10(filter.month.trim()).map(::toDomain)

    override suspend fun listKy11(filter: KyMonthFilterParam): List<Ky11Entry> =
        api.listKy11(filter.month.trim()).map(::toDomain)

    override suspend fun listKy12(filter: KyMonthFilterParam): List<Ky12Entry> =
        api.listKy12(filter.month.trim()).map(::toDomain)

    private fun toDomain(d: Ky9Dto) = Ky9Entry(
        id = d.id,
        date = d.date,
        drugName = d.drugName,
        regNo = d.regNo,
        unit = d.unit,
        qty = d.qty,
        pricePerUnit = d.pricePerUnit,
        totalValue = d.totalValue,
        seller = d.seller,
        invoiceNo = d.invoiceNo,
        createdAt = d.createdAt,
    )

    private fun toDomain(d: Ky10Dto) = Ky10Entry(
        id = d.id,
        date = d.date,
        drugName = d.drugName,
        regNo = d.regNo,
        qty = d.qty,
        unit = d.unit,
        buyerName = d.buyerName,
        buyerAddress = d.buyerAddress,
        rxNo = d.rxNo,
        doctor = d.doctor,
        balance = d.balance,
        createdAt = d.createdAt,
    )

    private fun toDomain(d: Ky11Dto) = Ky11Entry(
        id = d.id,
        date = d.date,
        drugName = d.drugName,
        regNo = d.regNo,
        qty = d.qty,
        unit = d.unit,
        buyerName = d.buyerName,
        purpose = d.purpose,
        pharmacist = d.pharmacist,
        createdAt = d.createdAt,
    )

    private fun toDomain(d: Ky12Dto) = Ky12Entry(
        id = d.id,
        date = d.date,
        rxNo = d.rxNo,
        patientName = d.patientName,
        doctor = d.doctor,
        hospital = d.hospital,
        drugName = d.drugName,
        qty = d.qty,
        unit = d.unit,
        totalValue = d.totalValue,
        status = d.status,
        createdAt = d.createdAt,
    )
}
