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
import app.devper.pharm.domain.parser.Ky11DraftBuilder
import app.devper.pharm.domain.usecase.AddKy11UseCase
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

data class Ky11Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val qty: String = "",
    val unit: String = "",
    val buyerName: String = "",
    val purpose: String = "",
    val pharmacist: String = "",
)

data class Ky11AddUiState(
    val draft: Ky11Draft = Ky11Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && Ky11DraftBuilder.isDraftValid(draft.date, draft.drugName, draft.unit, draft.qty)
}

class Ky11AddViewModel(
    private val addKy11: AddKy11UseCase,
) : BaseViewModel<Ky11AddUiState>(Ky11AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onBuyerName(v: String) = patch { copy(buyerName = v) }
    fun onPurpose(v: String) = patch { copy(purpose = v) }
    fun onPharmacist(v: String) = patch { copy(pharmacist = v) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val form = Ky11DraftBuilder.build(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            qty = s.draft.qty,
            unit = s.draft.unit,
            buyerName = s.draft.buyerName,
            purpose = s.draft.purpose,
            pharmacist = s.draft.pharmacist,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy11(form) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

    private fun patch(transform: Ky11Draft.() -> Ky11Draft) {
        setState { copy(draft = draft.transform()) }
    }
}

@Composable
fun Ky11AddScreen(
    onBack: () -> Unit,
    viewModel: Ky11AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = "เพิ่มรายการ ขย.11",
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
            PharmFormCard(title = "ข้อมูลรายการ ขย.11") {
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
                    FormField(label = "จำนวน", required = true) {
                        PharmTextField(
                            value = state.draft.qty,
                            onValueChange = viewModel::onQty,
                            keyboardType = KeyboardType.Number,
                        )
                    }
                    FormField(label = "ชื่อผู้ซื้อ") {
                        PharmTextField(value = state.draft.buyerName, onValueChange = viewModel::onBuyerName)
                    }
                    FormField(label = "วัตถุประสงค์การใช้") {
                        PharmTextField(
                            value = state.draft.purpose,
                            onValueChange = viewModel::onPurpose,
                            singleLine = false,
                        )
                    }
                    FormField(label = "ชื่อเภสัชกร") {
                        PharmTextField(value = state.draft.pharmacist, onValueChange = viewModel::onPharmacist)
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}
