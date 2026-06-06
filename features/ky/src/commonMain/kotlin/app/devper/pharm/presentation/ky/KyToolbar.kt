package app.devper.pharm.presentation.ky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmTab
import app.devper.pharm.ui.designsystem.PharmTabBar
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

private const val KY_TAB_PREFIX = "ky"

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
    val meta = kyFormMeta(currentForm)
    PharmListToolbar(
        title = meta.title,
        modifier = modifier,
        subtitle = meta.subtitle,
        filters = {
            KyFormTabs(currentForm = currentForm, onSwitchForm = onSwitchForm)
            KyMonthField(month = month, onMonthChange = onMonthChange, onApply = onApply)
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PharmButton(
                    label = "Excel",
                    onClick = onExportExcel,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Excel, contentDescription = null) },
                )
                PharmButton(
                    label = if (exporting) "กำลังส่งออก…" else "PDF",
                    onClick = onExport,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    enabled = !exporting,
                    leadingIcon = { Icon(PharmIcons.FilePdf, contentDescription = null) },
                )
                PharmButton(
                    label = "เพิ่มรายการ",
                    onClick = onAddEntry,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                )
            }
        },
    )
}

@Composable
private fun KyMonthField(
    month: String,
    onMonthChange: (String) -> Unit,
    onApply: () -> Unit,
) {
    val t = pharmTokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("เดือน", style = PharmText.micro.copy(color = t.colors.fg3))
        Box(modifier = Modifier.widthIn(min = 120.dp, max = 160.dp)) {
            PharmTextField(
                value = month,
                onValueChange = onMonthChange,
                placeholder = "YYYY-MM",
            )
        }
        PharmButton(
            label = "ค้นหา",
            onClick = onApply,
            variant = PharmButtonVariant.Outline,
            size = PharmButtonSize.Sm,
        )
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
        Text(text = "มูลค่ารวม", style = PharmText.micro.copy(color = t.colors.fg3))
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
        PharmTab(id = "$KY_TAB_PREFIX${form.number}", label = "ขย.${form.number}")
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
