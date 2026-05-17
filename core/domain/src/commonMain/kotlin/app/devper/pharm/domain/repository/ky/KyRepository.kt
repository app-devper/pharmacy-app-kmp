package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.param.AddKy9Param
import app.devper.pharm.domain.param.KyMonthFilterParam

interface KyRepository {

    suspend fun submitKy10(form: KyForm.Ky10)
    suspend fun submitKy11(form: KyForm.Ky11)
    suspend fun submitKy12(form: KyForm.Ky12)

    suspend fun addKy9(param: AddKy9Param)

    suspend fun listKy9(filter: KyMonthFilterParam): List<Ky9Entry>
    suspend fun listKy10(filter: KyMonthFilterParam): List<Ky10Entry>
    suspend fun listKy11(filter: KyMonthFilterParam): List<Ky11Entry>
    suspend fun listKy12(filter: KyMonthFilterParam): List<Ky12Entry>
}
