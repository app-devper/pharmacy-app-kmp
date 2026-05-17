package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.param.AddKy9Param
import app.devper.pharm.domain.param.KyMonthFilterParam

class FakeKyRepository(
    private val ky10Throws: Boolean = false,
    private val ky11Throws: Boolean = false,
    private val ky12Throws: Boolean = false,
    private val ky9Throws: Boolean = false,
) : KyRepository {

    val ky10Submissions = mutableListOf<KyForm.Ky10>()
    val ky11Submissions = mutableListOf<KyForm.Ky11>()
    val ky12Submissions = mutableListOf<KyForm.Ky12>()
    val ky9Adds = mutableListOf<AddKy9Param>()

    override suspend fun submitKy10(form: KyForm.Ky10) {
        if (ky10Throws) throw RuntimeException("ky10 failed")
        ky10Submissions += form
    }

    override suspend fun submitKy11(form: KyForm.Ky11) {
        if (ky11Throws) throw RuntimeException("ky11 failed")
        ky11Submissions += form
    }

    override suspend fun submitKy12(form: KyForm.Ky12) {
        if (ky12Throws) throw RuntimeException("ky12 failed")
        ky12Submissions += form
    }

    override suspend fun addKy9(param: AddKy9Param) {
        if (ky9Throws) throw RuntimeException("ky9 failed")
        ky9Adds += param
    }

    override suspend fun listKy9(filter: KyMonthFilterParam): List<Ky9Entry> = emptyList()
    override suspend fun listKy10(filter: KyMonthFilterParam): List<Ky10Entry> = emptyList()
    override suspend fun listKy11(filter: KyMonthFilterParam): List<Ky11Entry> = emptyList()
    override suspend fun listKy12(filter: KyMonthFilterParam): List<Ky12Entry> = emptyList()
}
