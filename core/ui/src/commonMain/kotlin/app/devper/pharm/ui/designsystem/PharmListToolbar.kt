package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PharmListToolbar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    searchValue: String? = null,
    onSearchChange: ((String) -> Unit)? = null,
    searchPlaceholder: String = "",
    titleStyle: TextStyle = PharmText.h1,
    badge: (@Composable () -> Unit)? = null,
    filters: (@Composable FlowRowScope.() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = titleStyle)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = PharmText.micro.copy(color = t.colors.fgMuted),
                    )
                }
            }
            badge?.invoke()
            if (searchValue != null && onSearchChange != null) {
                Box(modifier = Modifier.weight(1f).widthIn(max = 280.dp)) {
                    PharmTextField(
                        value = searchValue,
                        onValueChange = onSearchChange,
                        placeholder = searchPlaceholder,
                    )
                }
            }
            actions?.invoke()
        }
        if (filters != null) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = filters,
            )
        }
    }
}
