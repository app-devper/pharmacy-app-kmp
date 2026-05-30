package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.pricing.Tier
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun CartCustomerPill(
    customer: Customer?,
    activeTier: String,
    onPick: () -> Unit,
    onClear: () -> Unit,
    showShortcutHint: Boolean = false,
) {
    val t = pharmTokens
    val name = customer?.name ?: "ลูกค้าทั่วไป"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(t.shapes.md)
            .background(t.colors.accentBgSoft)
            .clickable(onClick = onPick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "ลูกค้า",
                style = PharmText.micro.copy(color = t.colors.accent.copy(alpha = 0.7f)),
            )
            Text(
                name,
                style = PharmText.bodySm.copy(
                    color = t.colors.accent,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
        if (customer != null) {
            Box(
                modifier = Modifier
                    .clip(t.shapes.sm)
                    .clickable(role = Role.Button, onClick = onClear)
                    .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                    .semantics { contentDescription = "ล้างลูกค้า" }
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("×", style = PharmText.h2.copy(color = t.colors.accent))
            }
        } else {
            if (showShortcutHint) {
                ShortcutHint(label = "F2", modifier = Modifier.padding(end = 6.dp))
            }
            Text(
                "›",
                style = PharmText.body.copy(color = t.colors.accent),
            )
        }

        if (customer != null && activeTier.isNotBlank() && activeTier != Tier.Retail) {
            Text(
                text = " · $activeTier",
                style = PharmText.micro.copy(color = t.colors.accent.copy(alpha = 0.7f)),
            )
        }
    }
}

@Preview
@Composable
private fun CartCustomerPill_Empty_Preview() {
    PharmacyTheme {
        CartCustomerPill(customer = null, activeTier = "RETAIL", onPick = {}, onClear = {})
    }
}

@Preview
@Composable
private fun CartCustomerPill_WithCustomer_Preview() {
    PharmacyTheme {
        CartCustomerPill(
            customer = Customer(id = "c1", name = "คุณสมชาย", phone = null, priceTier = "WHOLESALE", allergyNote = null),
            activeTier = "WHOLESALE",
            onPick = {},
            onClear = {},
        )
    }
}
