package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmFormCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = title, style = PharmText.h2)
                if (subtitle != null) {
                    Text(text = subtitle, style = PharmText.micro)
                }
            }
            trailing?.invoke()
        }
        content()
    }
}
