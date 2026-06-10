package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun CartSlotRail(
    slots: List<ParkedCart?>,
    selectedSlot: Int,
    onTapSlot: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(34.dp)
            .padding(vertical = 10.dp, horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        slots.forEachIndexed { index, parked ->
            SlotChip(
                number = index + 1,
                filled = parked != null,
                selected = index == selectedSlot,
                onClick = { onTapSlot(index) },
            )
        }
    }
}

@Composable
private fun SlotChip(
    number: Int,
    filled: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    val parkSlotDesc = pharmStrings.sellParkSlotDesc(number)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .background(
                    color = if (selected) t.colors.accent else Color.Transparent,
                    shape = RoundedCornerShape(2.dp),
                ),
        )
        Surface(
            color = if (filled) t.colors.accentBgSoft else t.colors.surface,
            contentColor = if (filled) t.colors.accent else t.colors.fg2,
            shape = CircleShape,
            border = if (filled) null else BorderStroke(1.dp, t.colors.border.copy(alpha = 0.4f)),
            modifier = Modifier
                .size(26.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics { contentDescription = parkSlotDesc },
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    style = PharmText.bodySm,
                    fontWeight = if (filled) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private val sampleParkedCart = ParkedCart(items = emptyList(), activeTier = "retail", parkedAt = 0L)

@Preview
@Composable
private fun CartSlotRail_MixedSlots_Preview() {
    PharmacyTheme {
        CartSlotRail(
            slots = listOf(sampleParkedCart, null, sampleParkedCart, null, null),
            selectedSlot = 0,
            onTapSlot = {},
        )
    }
}
