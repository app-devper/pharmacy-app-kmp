package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmStickyTotalRow(
    label: String,
    totalText: String,
    modifier: Modifier = Modifier,
    subtotalText: String? = null,
) {
    val t = pharmTokens
    Column(modifier = modifier.fillMaxWidth().background(t.colors.bgPage)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = label, style = PharmText.meta)
                if (subtotalText != null) {
                    Text(text = subtotalText, style = PharmText.micro)
                }
            }
            Text(text = totalText, style = PharmText.total)
        }
    }
}

@Composable
fun PharmStickyTotalBaht(
    label: String,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    subtotalText: String? = null,
) {
    PharmStickyTotalRow(
        label = label,
        totalText = fmtBaht(totalAmount),
        subtotalText = subtotalText,
        modifier = modifier,
    )
}
