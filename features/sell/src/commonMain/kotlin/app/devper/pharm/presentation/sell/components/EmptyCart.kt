package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EmptyCart(
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
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
            Text(text = "🛒", style = PharmText.body.copy(fontSize = 40.sp))
        }
        Text(
            text = "ตะกร้าว่างเปล่า",
            style = PharmText.body.copy(
                fontWeight = FontWeight.SemiBold,
                color = t.colors.fg1,
            ),
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = if (compact) "แตะรายการยาด้านบนเพื่อเริ่มขาย"
            else "แตะรายการยาทางซ้ายเพื่อเริ่มขาย",
            style = PharmText.meta,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Preview
@Composable
private fun EmptyCart_Wide_Preview() {
    PharmacyTheme { EmptyCart(compact = false) }
}

@Preview
@Composable
private fun EmptyCart_Compact_Preview() {
    PharmacyTheme { EmptyCart(compact = true) }
}
