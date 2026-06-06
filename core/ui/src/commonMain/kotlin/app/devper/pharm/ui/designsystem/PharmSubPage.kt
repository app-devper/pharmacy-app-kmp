package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmSubPage(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    contentSpacing: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        PharmSubPageHeader(
            title = title,
            onBack = onBack,
            subtitle = subtitle,
            actions = actions,
        )

        val bodyScroll = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(bodyScroll)
                .padding(contentPadding),
            verticalArrangement = if (contentSpacing) Arrangement.spacedBy(12.dp) else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )

        bottomBar?.invoke()
    }
}

@Composable
fun PharmSubPageHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    PharmListToolbar(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        onBack = onBack,
        titleStyle = PharmText.h2,
        actions = if (actions != null) {
            {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
            }
        } else {
            null
        },
    )
}
