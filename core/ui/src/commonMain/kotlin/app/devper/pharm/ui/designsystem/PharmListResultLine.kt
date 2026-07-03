package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmListResultLine(
    total: Int,
    noun: String,
    modifier: Modifier = Modifier,
    visible: Int = total,
    searching: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    val text = if (searching) pharmStrings.commonResultFound(visible, noun, total) else pharmStrings.commonResultTotal(total, noun)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = PharmText.micro.copy(color = t.colors.fg3))
        trailing?.invoke()
    }
}
