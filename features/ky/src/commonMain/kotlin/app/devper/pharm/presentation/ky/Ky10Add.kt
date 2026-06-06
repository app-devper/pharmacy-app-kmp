package app.devper.pharm.presentation.ky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.parser.Ky10DraftBuilder
import app.devper.pharm.domain.usecase.AddKy10UseCase
import app.devper.pharm.ui.common.BaseUiState
import app.devper.pharm.ui.common.BaseViewModel
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmSubPage
import app.devper.pharm.ui.designsystem.PharmTextField
import org.koin.compose.viewmodel.koinViewModel

data class Ky10Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val qty: String = "",
    val unit: String = "",
    val buyerName: String = "",
    val buyerAddress: String = "",
    val rxNo: String = "",
    val doctor: String = "",
    val balance: String = "",
)

data class Ky10AddUiState(
    val draft: Ky10Draft = Ky10Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && Ky10DraftBuilder.isDraftValid(draft.date, draft.drugName, draft.unit, draft.qty)
}

class Ky10AddViewModel(
    private val addKy10: AddKy10UseCase,
) : BaseViewModel<Ky10AddUiState>(Ky10AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onBuyerName(v: String) = patch { copy(buyerName = v) }
    fun onBuyerAddress(v: String) = patch { copy(buyerAddress = v) }
    fun onRxNo(v: String) = patch { copy(rxNo = v) }
    fun onDoctor(v: String) = patch { copy(doctor = v) }
    fun onBalance(v: String) = patch { copy(balance = v.filter { it.isDigit() }) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val form = Ky10DraftBuilder.build(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            qty = s.draft.qty,
            unit = s.draft.unit,
            buyerName = s.draft.buyerName,
            buyerAddress = s.draft.buyerAddress,
            rxNo = s.draft.rxNo,
            doctor = s.draft.doctor,
            balance = s.draft.balance,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy10(form) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

    private fun patch(transform: Ky10Draft.() -> Ky10Draft) {
        setState { copy(draft = draft.transform()) }
    }
}

@Composable
fun Ky10AddScreen(
    onBack: () -> Unit,
    viewModel: Ky10AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    PharmSubPage(
        title = "เพิ่มรายการ ขย.10",
        onBack = onBack,
        scrollable = true,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        actions = {
            PharmSaveAction(
                saving = state.saving,
                canSubmit = state.canSubmitDraft,
                onSubmit = viewModel::submitAdd,
            )
        },
    ) {
        Column(
            modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = "ข้อมูลรายการ ขย.10") {
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
                            FormField(label = "ยอดคงเหลือ") {
                                PharmTextField(
                                    value = state.draft.balance,
                                    onValueChange = viewModel::onBalance,
                                    keyboardType = KeyboardType.Number,
                                )
                            }
                        },
                    )
                    FormField(label = "ชื่อผู้ซื้อ") {
                        PharmTextField(value = state.draft.buyerName, onValueChange = viewModel::onBuyerName)
                    }
                    FormField(label = "ที่อยู่ผู้ซื้อ") {
                        PharmTextField(
                            value = state.draft.buyerAddress,
                            onValueChange = viewModel::onBuyerAddress,
                            singleLine = false,
                        )
                    }
                    KyTwoUp(
                        left = {
                            FormField(label = "เลขที่ใบสั่ง (Rx)") {
                                PharmTextField(value = state.draft.rxNo, onValueChange = viewModel::onRxNo)
                            }
                        },
                        right = {
                            FormField(label = "แพทย์ผู้สั่ง") {
                                PharmTextField(value = state.draft.doctor, onValueChange = viewModel::onDoctor)
                            }
                        },
                    )
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}
