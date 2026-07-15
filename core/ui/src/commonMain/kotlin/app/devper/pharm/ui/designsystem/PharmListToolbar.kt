package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

private val TITLE_MIN_WIDTH = 600.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PharmListToolbar(
    title: String = "",
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    searchValue: String? = null,
    onSearchChange: ((String) -> Unit)? = null,
    searchPlaceholder: String = "",
    titleStyle: TextStyle = PharmText.h1,
    badge: (@Composable () -> Unit)? = null,
    filters: (@Composable FlowRowScope.() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val showTitle = title.isNotEmpty() && (onBack != null || maxWidth >= TITLE_MIN_WIDTH)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(t.shapes.md)
                            .pharmClickable(role = Role.Button, shape = t.shapes.md, onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = PharmIcons.ReturnArrow,
                            contentDescription = pharmStrings.commonBack,
                            tint = t.colors.fg1,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                if (showTitle) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = titleStyle)
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = PharmText.micro.copy(color = t.colors.fgMuted),
                            )
                        }
                    }
                }
                badge?.invoke()
                if (searchValue != null && onSearchChange != null) {
                    Box(modifier = Modifier.widthIn(min = 160.dp, max = 280.dp)) {
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
                    itemVerticalAlignment = Alignment.CenterVertically,
                    content = filters,
                )
            }
        }
    }
}
