package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun EmptyCart(modifier: Modifier = Modifier) {
    val t = pharmTokens
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(t.shapes.lg)
                .background(t.colors.accentBgSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = PharmIcons.Sell,
                contentDescription = null,
                tint = t.colors.fgMuted,
                modifier = Modifier.size(40.dp),
            )
        }
        Text(
            text = pharmStrings.sellEmptyCart,
            style = PharmText.body.copy(
                fontWeight = FontWeight.SemiBold,
                color = t.colors.fg1,
            ),
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = pharmStrings.sellEmptyCartHint,
            style = PharmText.meta,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Preview
@Composable
private fun EmptyCart_Wide_Preview() {
    PharmacyTheme { EmptyCart() }
}

@Preview
@Composable
private fun EmptyCart_Compact_Preview() {
    PharmacyTheme { EmptyCart() }
}
