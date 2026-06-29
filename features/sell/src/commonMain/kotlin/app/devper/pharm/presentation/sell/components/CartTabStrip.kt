package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
internal fun CartTabStrip(
    slots: List<ParkedCart?>,
    activeSlot: Int,
    onTapSlot: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        slots.forEachIndexed { index, parked ->
            TabChip(
                number = index + 1,
                filled = parked != null,
                selected = index == activeSlot,
                onClick = { onTapSlot(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TabChip(
    number: Int,
    filled: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val parkSlotDesc = pharmStrings.sellParkSlotDesc(number)
    val bg = when {
        selected -> t.colors.accentBgSoft
        filled   -> t.colors.sidebarItemActive
        else     -> t.colors.surface
    }
    val fg = when {
        selected -> t.colors.accent
        filled   -> t.colors.sidebarFg
        else     -> t.colors.fg2
    }
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(t.shapes.md)
            .background(bg)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = parkSlotDesc },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            style = PharmText.buttonMd,
            color = fg,
            fontWeight = if (selected || filled) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
        if (filled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 7.dp)
                    .size(7.dp)
                    .background(color = t.colors.successFg, shape = CircleShape),
            )
        }
    }
}

private val sampleParkedCart = ParkedCart(items = emptyList(), activeTier = "retail", parkedAt = 0L)

@Preview
@Composable
private fun CartTabStrip_Preview() {
    PharmacyTheme {
        CartTabStrip(
            slots = listOf(null, sampleParkedCart, null, sampleParkedCart, null),
            activeSlot = 0,
            onTapSlot = {},
        )
    }
}
