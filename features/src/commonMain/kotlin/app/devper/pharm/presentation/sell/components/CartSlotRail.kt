package app.devper.pharm.presentation.sell.components
import app.devper.pharm.presentation.sell.sibling.ParkedCartViewModel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ParkedCart

@Composable
internal fun CartSlotRail(
    slots: List<ParkedCart?>,
    onTapSlot: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(40.dp)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        slots.forEachIndexed { index, parked ->
            SlotChip(
                number = index + 1,
                filled = parked != null,
                onClick = { onTapSlot(index) },
            )
        }
    }
}

@Composable
private fun SlotChip(
    number: Int,
    filled: Boolean,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.size(32.dp)) {
        Surface(
            color = if (filled) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface,
            contentColor = if (filled) MaterialTheme.colorScheme.onPrimaryContainer
                           else MaterialTheme.colorScheme.onSurfaceVariant,
            shape = CircleShape,
            border = if (filled) null
                     else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = onClick),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (filled) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (filled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
            )
        }
    }
}
