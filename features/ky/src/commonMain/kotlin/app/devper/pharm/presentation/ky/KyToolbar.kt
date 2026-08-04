package app.devper.pharm.presentation.ky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSearchAction
import app.devper.pharm.ui.designsystem.PharmTab
import app.devper.pharm.ui.designsystem.PharmTabBar
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

private const val KY_TAB_PREFIX = "ky"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun KyToolbar(
    currentForm: KyFormType,
    onSwitchForm: (KyFormType) -> Unit,
    month: String,
    onMonthChange: (String) -> Unit,
    onApply: () -> Unit,
    onExport: () -> Unit,
    exporting: Boolean,
    onExportExcel: () -> Unit = {},
    onAddEntry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val meta = kyFormMeta(currentForm, pharmStrings)
    PharmListToolbar(
        modifier = modifier,
        subtitle = pharmStrings.kyToolbarSubtitle,
        compactControlsSharedRow = false,
        filters = {
            KyFormTabs(currentForm = currentForm, onSwitchForm = onSwitchForm)
            KyMonthField(month = month, onMonthChange = onMonthChange, onApply = onApply)
        },
        actions = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                PharmActionMenu(
                    actions = listOf(
                        PharmAction(
                            label = "Excel",
                            onClick = onExportExcel,
                            icon = PharmIcons.Excel,
                        ),
                        PharmAction(
                            label = if (exporting) pharmStrings.kyExportingPdf else "PDF",
                            onClick = onExport,
                            icon = PharmIcons.FilePdf,
                            enabled = !exporting,
                        ),
                    ),
                )
                PharmButton(
                    label = pharmStrings.kyAddCta,
                    onClick = onAddEntry,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                )
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KyMonthField(
    month: String,
    onMonthChange: (String) -> Unit,
    onApply: () -> Unit,
) {
    val t = pharmTokens
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(pharmStrings.kyMonthLabel, style = PharmText.micro.copy(color = t.colors.fg3))
        Box(modifier = Modifier.widthIn(min = 120.dp, max = 160.dp)) {
            PharmTextField(
                value = month,
                onValueChange = onMonthChange,
                placeholder = "YYYY-MM",
                imeAction = ImeAction.Search,
                onImeAction = onApply,
                trailingSlot = { PharmSearchAction(onClick = onApply) },
            )
        }
    }
}

@Composable
internal fun KyValueStat(totalValue: Double, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = pharmStrings.kyTotalValue, style = PharmText.micro.copy(color = t.colors.fg3))
        Text(
            text = fmtBaht(totalValue),
            style = PharmText.bodySm.copy(
                color = t.colors.fg1,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum",
            ),
        )
    }
}

@Composable
private fun KyFormTabs(
    currentForm: KyFormType,
    onSwitchForm: (KyFormType) -> Unit,
) {
    val tabs = KyFormType.entries.map { form ->
        PharmTab(id = "$KY_TAB_PREFIX${form.number}", label = pharmStrings.kyTabLabel(form.number))
    }
    PharmTabBar(
        tabs = tabs,
        activeId = "$KY_TAB_PREFIX${currentForm.number}",
        onSelect = { id ->
            val n = id.removePrefix(KY_TAB_PREFIX).toIntOrNull() ?: return@PharmTabBar
            val target = KyFormType.entries.firstOrNull { it.number == n } ?: return@PharmTabBar
            if (target != currentForm) onSwitchForm(target)
        },
        fillMaxWidth = false,
    )
}
