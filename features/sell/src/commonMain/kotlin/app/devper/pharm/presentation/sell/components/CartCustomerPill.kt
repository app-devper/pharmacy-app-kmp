package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.designsystem.PharmIcons
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
    val s = pharmStrings
    val name = customer?.name ?: s.sellCustomerWalkIn
    val allergy = customer?.allergyNote?.takeIf { it.isNotBlank() && it != "-" }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(t.shapes.md)
            .background(t.colors.accentBgSoft)
            .clickable(role = Role.Button, onClick = onPick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                s.sellCustomer,
                style = PharmText.micro.copy(color = t.colors.accent.copy(alpha = 0.7f)),
            )
            Text(
                name,
                style = PharmText.bodySm.copy(
                    color = t.colors.accent,
                    fontWeight = FontWeight.Medium,
                ),
            )
            if (allergy != null) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        PharmIcons.Warning,
                        contentDescription = null,
                        tint = t.colors.warningFg,
                        modifier = Modifier.size(12.dp),
                    )
                    Column {
                        Text(
                            s.sellAllergyTitle,
                            style = PharmText.micro.copy(color = t.colors.warningFg),
                        )
                        Text(
                            allergy,
                            style = PharmText.micro.copy(color = t.colors.warningFg),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
        if (customer != null && activeTier.isNotBlank() && activeTier != Tier.Retail) {
            Text(
                text = "$activeTier · ",
                style = PharmText.micro.copy(color = t.colors.accent.copy(alpha = 0.7f)),
            )
        }
        val clearCustomerDesc = s.sellCustomerClear
        if (customer != null) {
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .size(32.dp)
                    .semantics { contentDescription = clearCustomerDesc },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = t.colors.accent,
                    modifier = Modifier.size(16.dp),
                )
            }
        } else {
            if (showShortcutHint) {
                ShortcutHint(label = "F3", modifier = Modifier.padding(end = 6.dp))
            }
            Icon(
                Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = t.colors.accent,
                modifier = Modifier.size(20.dp),
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

@Preview
@Composable
private fun CartCustomerPill_WithAllergy_Preview() {
    PharmacyTheme {
        CartCustomerPill(
            customer = Customer(id = "c2", name = "คุณสมหญิง", phone = null, priceTier = "WHOLESALE", allergyNote = "แพ้ Penicillin"),
            activeTier = "WHOLESALE",
            onPick = {},
            onClear = {},
        )
    }
}

@Preview
@Composable
private fun CartCustomerPill_DashAllergy_Preview() {
    PharmacyTheme {
        CartCustomerPill(
            customer = Customer(id = "c3", name = "คุณมานะ", phone = null, priceTier = "RETAIL", allergyNote = "-"),
            activeTier = "RETAIL",
            onPick = {},
            onClear = {},
        )
    }
}
