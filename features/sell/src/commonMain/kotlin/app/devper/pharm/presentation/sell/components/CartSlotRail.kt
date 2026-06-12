package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.format.todayDayMonth
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun CartSlotRail(
    slots: List<ParkedCart?>,
    selectedSlot: Int,
    onTapSlot: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(44.dp)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = pharmStrings.calendarToday,
            style = PharmText.micro.copy(color = t.colors.fgMuted),
            textAlign = TextAlign.Center,
        )
        Text(
            text = todayDayMonth(),
            style = PharmText.bodySm.copy(color = t.colors.successFg, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(t.shapes.md)
            .background(if (selected) t.colors.accentBgSoft else Color.Transparent)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = parkSlotDesc },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            style = PharmText.buttonMd,
            color = if (selected) t.colors.accent else t.colors.fg2,
            fontWeight = if (selected || filled) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
        if (filled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 6.dp)
                    .size(7.dp)
                    .background(color = t.colors.successFg, shape = CircleShape),
            )
        }
    }
}

private val sampleParkedCart = ParkedCart(items = emptyList(), activeTier = "retail", parkedAt = 0L)

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun CartSlotRail_MixedSlots_Preview() {
    app.devper.pharm.ui.theme.PharmacyTheme {
        CartSlotRail(
            slots = listOf(sampleParkedCart, null, sampleParkedCart, null, null),
            selectedSlot = 0,
            onTapSlot = {},
        )
    }
}
