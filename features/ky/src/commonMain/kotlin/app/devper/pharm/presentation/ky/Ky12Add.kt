package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.parser.Ky12DraftBuilder
import app.devper.pharm.domain.usecase.AddKy12UseCase
import app.devper.pharm.ui.common.BaseUiState
import app.devper.pharm.ui.common.BaseViewModel
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

data class Ky12Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val qty: String = "",
    val unit: String = "",
    val rxNo: String = "",
    val patientName: String = "",
    val doctor: String = "",
    val hospital: String = "",
    val totalValue: String = "",
    val status: String = "จ่ายแล้ว",
)

data class Ky12AddUiState(
    val draft: Ky12Draft = Ky12Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && Ky12DraftBuilder.isDraftValid(draft.date, draft.drugName, draft.unit, draft.qty)
}

class Ky12AddViewModel(
    private val addKy12: AddKy12UseCase,
) : BaseViewModel<Ky12AddUiState>(Ky12AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onRxNo(v: String) = patch { copy(rxNo = v) }
    fun onPatientName(v: String) = patch { copy(patientName = v) }
    fun onDoctor(v: String) = patch { copy(doctor = v) }
    fun onHospital(v: String) = patch { copy(hospital = v) }
    fun onTotalValue(v: String) = patch { copy(totalValue = v.numericMoneyKy12()) }
    fun onStatus(v: String) = patch { copy(status = v) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val form = Ky12DraftBuilder.build(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            qty = s.draft.qty,
            unit = s.draft.unit,
            rxNo = s.draft.rxNo,
            patientName = s.draft.patientName,
            doctor = s.draft.doctor,
            hospital = s.draft.hospital,
            totalValue = s.draft.totalValue,
            status = s.draft.status,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy12(form) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

    private fun patch(transform: Ky12Draft.() -> Ky12Draft) {
        setState { copy(draft = draft.transform()) }
    }
}

@Composable
fun Ky12AddScreen(
    onBack: () -> Unit,
    viewModel: Ky12AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = "เพิ่มรายการ ขย.12",
            onBack = onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmitDraft,
                    onSubmit = viewModel::submitAdd,
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = "ข้อมูลรายการ ขย.12") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KyTwoUp(
                        left = {
                            FormField(label = "วันที่ (YYYY-MM-DD)", required = true) {
                                PharmTextField(value = state.draft.date, onValueChange = viewModel::onDate)
                            }
                        },
                        right = {
                            FormField(label = "ชื่อยา", required = true) {
                                PharmTextField(value = state.draft.drugName, onValueChange = viewModel::onDrugName)
                            }
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = "เลขทะเบียน") {
                                PharmTextField(value = state.draft.regNo, onValueChange = viewModel::onRegNo)
                            }
                        },
                        right = {
                            FormField(label = "หน่วย", required = true) {
                                PharmTextField(value = state.draft.unit, onValueChange = viewModel::onUnit)
                            }
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = "จำนวน", required = true) {
                                PharmTextField(
                                    value = state.draft.qty,
                                    onValueChange = viewModel::onQty,
                                    keyboardType = KeyboardType.Number,
                                )
                            }
                        },
                        right = {
                            FormField(label = "มูลค่ารวม") {
                                PharmTextField(
                                    value = state.draft.totalValue,
                                    onValueChange = viewModel::onTotalValue,
                                    keyboardType = KeyboardType.Decimal,
                                )
                            }
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = "เลขที่ใบสั่งยา (Rx)") {
                                PharmTextField(value = state.draft.rxNo, onValueChange = viewModel::onRxNo)
                            }
                        },
                        right = {
                            FormField(label = "ชื่อคนไข้") {
                                PharmTextField(value = state.draft.patientName, onValueChange = viewModel::onPatientName)
                            }
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = "แพทย์ผู้สั่ง") {
                                PharmTextField(value = state.draft.doctor, onValueChange = viewModel::onDoctor)
                            }
                        },
                        right = {
                            FormField(label = "โรงพยาบาล/คลินิก") {
                                PharmTextField(value = state.draft.hospital, onValueChange = viewModel::onHospital)
                            }
                        },
                    )
                    FormField(label = "สถานะ") {
                        PharmTextField(
                            value = state.draft.status,
                            onValueChange = viewModel::onStatus,
                            placeholder = "เช่น จ่ายแล้ว / รอจ่าย",
                        )
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}

private fun String.numericMoneyKy12(): String {
    val filtered = filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) filtered
    else filtered.substring(0, firstDot + 1) +
        filtered.substring(firstDot + 1).filter { it != '.' }
}
