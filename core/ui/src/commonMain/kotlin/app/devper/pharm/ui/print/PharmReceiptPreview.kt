package app.devper.pharm.ui.print

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.common.print.ReceiptLine
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.ui.format.formatBaht
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.TabularNumbers
import app.devper.pharm.ui.theme.pharmTokens

@Immutable
data class PharmReceiptStyle(
    val width: Dp? = ReceiptWidth80mm,
    val padding: Dp = 16.dp,
    val rowSpacing: Dp = 4.dp,
    val sectionSpacing: Dp = 10.dp,
    val paperBg: Color? = null,
    val inkColor: Color? = null,
    val mutedInkColor: Color? = null,
    val dividerColor: Color? = null,
    val accentColor: Color? = null,
    val borderColor: Color? = null,
    val fontFamily: FontFamily? = null,
    val showStoreHeader: Boolean = true,
    val showItems: Boolean = true,
    val showFooter: Boolean = true,
)

val ReceiptWidth58mm: Dp = 220.dp
val ReceiptWidth80mm: Dp = 280.dp

@Composable
fun PharmReceiptPreview(
    template: ReceiptTemplate,
    style: PharmReceiptStyle = PharmReceiptStyle(),
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val paper = style.paperBg ?: t.colors.surface
    val ink = style.inkColor ?: t.colors.fg1
    val muted = style.mutedInkColor ?: t.colors.fg3
    val divider = style.dividerColor ?: t.colors.border
    val accent = style.accentColor ?: t.colors.accent
    val borderColor = style.borderColor ?: t.colors.borderSubtle
    val fontFamily = style.fontFamily

    val sizedModifier = if (style.width != null) modifier.width(style.width) else modifier

    Column(
        modifier = sizedModifier
            .clip(t.shapes.md)
            .background(paper, t.shapes.md)
            .border(1.dp, borderColor, t.shapes.md)
            .padding(style.padding),
        verticalArrangement = Arrangement.spacedBy(style.rowSpacing),
    ) {
        if (style.showStoreHeader) {
            ReceiptHeader(template, ink, muted, fontFamily)
            DashedDivider(color = divider)
        }

        ReceiptMeta(template, ink, muted, fontFamily)

        if (style.showItems && template.items.isNotEmpty()) {
            DashedDivider(color = divider)
            ReceiptItems(template.items, ink, muted, fontFamily)
        }

        DashedDivider(color = divider)

        ReceiptTotals(template, ink, muted, accent, fontFamily)

        if (style.showFooter) {
            DashedDivider(color = divider)
            ReceiptFooter(template, muted, fontFamily)
        }
    }
}

@Composable
private fun ReceiptHeader(
    template: ReceiptTemplate,
    ink: Color,
    muted: Color,
    fontFamily: FontFamily?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (template.storeName.isNotBlank()) {
            Text(
                text = template.storeName,
                style = PharmText.h3.copy(color = ink, fontFamily = fontFamily),
                textAlign = TextAlign.Center,
            )
        }
        if (template.storeAddress.isNotBlank()) {
            Text(
                text = template.storeAddress,
                style = PharmText.micro.copy(color = muted, fontFamily = fontFamily),
                textAlign = TextAlign.Center,
            )
        }
        val contact = buildString {
            if (template.storePhone.isNotBlank()) append("โทร ${template.storePhone}")
            if (template.storeTaxId.isNotBlank()) {
                if (isNotEmpty()) append(" · ")
                append("เลขผู้เสียภาษี ${template.storeTaxId}")
            }
        }
        if (contact.isNotBlank()) {
            Text(
                text = contact,
                style = PharmText.micro.copy(color = muted, fontFamily = fontFamily),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReceiptMeta(
    template: ReceiptTemplate,
    ink: Color,
    muted: Color,
    fontFamily: FontFamily?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (template.billNo.isNotBlank()) {
            ReceiptLabelValue(
                label = "เลขบิล",
                value = template.billNo,
                ink = ink,
                muted = muted,
                fontFamily = fontFamily,
                emphasis = true,
            )
        }
        if (template.soldAt.isNotBlank()) {
            ReceiptLabelValue(
                label = "วันที่",
                value = template.soldAt,
                ink = ink,
                muted = muted,
                fontFamily = fontFamily,
            )
        }
        if (template.customerName.isNotBlank()) {
            Text(
                text = "ลูกค้า: ${template.customerName}",
                style = PharmText.micro.copy(color = muted, fontFamily = fontFamily),
            )
        }
    }
}

@Composable
private fun ReceiptItems(
    items: List<ReceiptLine>,
    ink: Color,
    muted: Color,
    fontFamily: FontFamily?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items.forEach { line ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = line.name,
                        style = PharmText.bodySm.copy(color = ink, fontFamily = fontFamily),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = formatBaht(line.lineTotal),
                        style = PharmText.bodySm.copy(
                            color = ink,
                            fontFamily = fontFamily,
                            fontFeatureSettings = TabularNumbers,
                        ),
                    )
                }
                Text(
                    text = "${line.displayQty} ${line.displayUnit} × ${formatBaht(line.unitPrice)}",
                    style = PharmText.micro.copy(
                        color = muted,
                        fontFamily = fontFamily,
                        fontFeatureSettings = TabularNumbers,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ReceiptTotals(
    template: ReceiptTemplate,
    ink: Color,
    muted: Color,
    accent: Color,
    fontFamily: FontFamily?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (template.itemDiscountTotal > 0.0) {
            ReceiptLabelValue(
                label = "ส่วนลดรายการ",
                value = "-${formatBaht(template.itemDiscountTotal)}",
                ink = ink,
                muted = muted,
                fontFamily = fontFamily,
            )
        }
        if (template.cartDiscount > 0.0) {
            ReceiptLabelValue(
                label = "ส่วนลดบิล",
                value = "-${formatBaht(template.cartDiscount)}",
                ink = ink,
                muted = muted,
                fontFamily = fontFamily,
            )
        }
        ReceiptLabelValue(
            label = "รวมสุทธิ",
            value = formatBaht(template.total),
            ink = ink,
            muted = muted,
            fontFamily = fontFamily,
            emphasis = true,
            accentValue = accent,
        )
        ReceiptLabelValue(
            label = "รับเงิน",
            value = formatBaht(template.received),
            ink = ink,
            muted = muted,
            fontFamily = fontFamily,
        )
        ReceiptLabelValue(
            label = "เงินทอน",
            value = formatBaht(template.change),
            ink = ink,
            muted = muted,
            fontFamily = fontFamily,
            emphasis = true,
        )
    }
}

@Composable
private fun ReceiptFooter(
    template: ReceiptTemplate,
    muted: Color,
    fontFamily: FontFamily?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (template.pharmacistName.isNotBlank()) {
            Text(
                text = "เภสัชกร: ${template.pharmacistName}",
                style = PharmText.micro.copy(color = muted, fontFamily = fontFamily),
                textAlign = TextAlign.Center,
            )
        }
        if (template.footer.isNotBlank()) {
            Text(
                text = template.footer,
                style = PharmText.micro.copy(color = muted, fontFamily = fontFamily),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReceiptLabelValue(
    label: String,
    value: String,
    ink: Color,
    muted: Color,
    fontFamily: FontFamily?,
    emphasis: Boolean = false,
    accentValue: Color? = null,
) {
    val labelStyle: TextStyle = PharmText.bodySm.copy(
        color = if (emphasis) ink else muted,
        fontFamily = fontFamily,
        fontWeight = if (emphasis) FontWeight.SemiBold else FontWeight.Normal,
    )
    val valueStyle: TextStyle = PharmText.bodySm.copy(
        color = accentValue ?: ink,
        fontFamily = fontFamily,
        fontFeatureSettings = TabularNumbers,
        fontWeight = if (emphasis) FontWeight.Bold else FontWeight.Normal,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = label, style = labelStyle, modifier = Modifier.weight(1f))
        Text(text = value, style = valueStyle)
    }
}

@Composable
internal fun DashedDivider(color: Color, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp),
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            pathEffect = pathEffect,
        )
    }
}
