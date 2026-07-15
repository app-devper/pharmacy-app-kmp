package app.devper.pharm.presentation.stock.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stock.DrugFormFields
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings

internal class TierPriceFocusRequesters {
    val retail = FocusRequester()
    val regular = FocusRequester()
    val wholesale = FocusRequester()
}

@Composable
internal fun TierPricingCard(
    form: DrugFormFields,
    callbacks: DrugFormCallbacks,
    focus: TierPriceFocusRequesters,
) {
    PharmFormCard(
        title = pharmStrings.stockPricingTitle,
        subtitle = pharmStrings.stockPricingSubtitle,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val fields = listOf(
                TierPriceFieldSpec(
                    label = pharmStrings.sellTierRetailLabel,
                    value = form.tierRetail,
                    onValueChange = callbacks.onTierRetail,
                    valid = form.tierRetailValid,
                    focusRequester = focus.retail,
                ),
                TierPriceFieldSpec(
                    label = pharmStrings.sellTierRegularLabel,
                    value = form.tierRegular,
                    onValueChange = callbacks.onTierRegular,
                    valid = form.tierRegularValid,
                    focusRequester = focus.regular,
                ),
                TierPriceFieldSpec(
                    label = pharmStrings.sellTierWholesaleLabel,
                    value = form.tierWholesale,
                    onValueChange = callbacks.onTierWholesale,
                    valid = form.tierWholesaleValid,
                    focusRequester = focus.wholesale,
                ),
            )
            if (maxWidth >= PharmBreakpoint.FormThreeCol) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    fields.forEach { TierPriceField(spec = it, modifier = Modifier.weight(1f)) }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    fields.forEach { TierPriceField(spec = it) }
                }
            }
        }
    }
}

private data class TierPriceFieldSpec(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val valid: Boolean,
    val focusRequester: FocusRequester,
)

@Composable
private fun TierPriceField(spec: TierPriceFieldSpec, modifier: Modifier = Modifier) {
    val error = if (spec.valid) null else pharmStrings.validationNotANumber(spec.label)
    FormField(label = spec.label, error = error, modifier = modifier) {
        PharmTextField(
            value = spec.value,
            onValueChange = spec.onValueChange,
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            isError = error != null,
            focusRequester = spec.focusRequester,
        )
    }
}
