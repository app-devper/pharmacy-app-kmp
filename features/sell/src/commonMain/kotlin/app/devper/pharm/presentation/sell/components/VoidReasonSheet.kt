package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun VoidReasonSheet(
    billNo: String,
    submitting: Boolean,
    onConfirm: (reason: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var reason by rememberSaveable(billNo) { mutableStateOf("") }
    var validationRequested by rememberSaveable(billNo) { mutableStateOf(false) }
    val reasonFocus = remember(billNo) { FocusRequester() }
    val reasonError = validationRequested && reason.isBlank()
    val submitReason: () -> Unit = {
        if (reason.isNotBlank()) {
            onConfirm(reason.trim())
        } else {
            validationRequested = true
            reasonFocus.requestFocus()
            Unit
        }
    }
    val t = pharmTokens

    PharmBottomSheet(
        onDismissRequest = onDismiss,
        dismissEnabled = !submitting,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = pharmStrings.sellVoidBillTitle(billNo), style = PharmText.h1)
            Text(
                text = pharmStrings.sellVoidBillSubtitle,
                style = PharmText.meta,
            )

            FormField(
                label = pharmStrings.sellCancelBillReason,
                required = true,
                error = if (reasonError) pharmStrings.sellVoidReasonRequired else null,
            ) {
                PharmTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = pharmStrings.sellCancelBillReasonExample,
                    singleLine = false,
                    imeAction = ImeAction.Done,
                    onImeAction = submitReason,
                    isError = reasonError,
                    focusRequester = reasonFocus,
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                PharmButton(
                    label = pharmStrings.commonCancel,
                    onClick = onDismiss,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                    enabled = !submitting,
                )
                PharmButton(
                    label = pharmStrings.sellCancelBillTitle,
                    onClick = submitReason,
                    variant = PharmButtonVariant.Danger,
                    size = PharmButtonSize.Md,
                    enabled = !submitting,
                    loading = submitting,
                )
            }
        }
    }
}
