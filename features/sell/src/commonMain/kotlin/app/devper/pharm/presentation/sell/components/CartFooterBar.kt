package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CartFooterBar(
    itemCount: Int,
    total: Double,
    onClick: () -> Unit,
    parkedFilledCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val empty = itemCount == 0
    val hasParked = parkedFilledCount > 0
    val tappable = !empty || hasParked

    val containerColor: Color = when {
        !empty    -> t.colors.accent
        hasParked -> t.colors.borderSubtle
        else      -> t.colors.bgPage
    }
    val contentColor: Color = if (!empty) t.colors.surface else t.colors.fg1

    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .clickable(enabled = tappable, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = PharmIcons.Sell,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp),
        )

        if (!empty) {

            Box(
                modifier = Modifier
                    .clip(t.shapes.pill)
                    .background(t.colors.surface.copy(alpha = 0.25f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "$itemCount",
                    style = PharmText.badge.copy(
                        color = t.colors.surface,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when {
                    !empty    -> "$itemCount รายการ"
                    hasParked -> "บิลที่พัก $parkedFilledCount รอเปิด"
                    else      -> "ตะกร้าว่าง"
                },
                style = PharmText.micro.copy(color = contentColor.copy(alpha = 0.8f)),
            )
            if (!empty) {
                Text(
                    text = fmtBaht(total),
                    style = PharmText.total.copy(color = contentColor),
                )
            }
        }

        if (tappable) {
            Text(
                text = if (!empty) "ดูตะกร้า ›" else "เปิดบิลที่พัก ›",
                style = PharmText.buttonMd.copy(
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        } else {
            Box(modifier = Modifier.size(0.dp))
        }
    }
}

@Preview
@Composable
private fun CartFooterBar_Empty_Preview() {
    PharmacyTheme { CartFooterBar(itemCount = 0, total = 0.0, onClick = {}) }
}

@Preview
@Composable
private fun CartFooterBar_ParkedOnly_Preview() {
    PharmacyTheme {
        CartFooterBar(itemCount = 0, total = 0.0, onClick = {}, parkedFilledCount = 2)
    }
}

@Preview
@Composable
private fun CartFooterBar_WithItems_Preview() {
    PharmacyTheme { CartFooterBar(itemCount = 4, total = 1245.50, onClick = {}) }
}
