package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.print.PharmReceiptPreview
import app.devper.pharm.ui.print.PharmReceiptStyle
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun EodClosedReceiptCard(
    report: EodReport,
    template: ReceiptTemplate?,
    onPrint: () -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.accent, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = PharmIcons.Check,
                contentDescription = null,
                tint = t.colors.successFg,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "ปิดรอบ EOD เรียบร้อย — วันที่ ${report.date.ifBlank { "วันนี้" }}",
                style = PharmText.h2,
                modifier = Modifier.weight(1f),
            )
            PharmButton(
                label = "พิมพ์",
                onClick = onPrint,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = {
                    Icon(
                        imageVector = PharmIcons.Print,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                },
            )
        }
        if (template != null) {
            PharmReceiptPreview(
                template = template,
                style = PharmReceiptStyle(
                    width = null,
                    padding = 12.dp,
                    showStoreHeader = false,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
