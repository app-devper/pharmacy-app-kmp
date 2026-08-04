package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onSearch: (() -> Unit)? = null,
    searching: Boolean = false,
    focusRequester: FocusRequester? = null,
    showSearchAction: Boolean = onSearch != null,
    endSlot: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    val searchAction = onSearch.takeIf { showSearchAction }
    val showDefaultEndSearchIcon = showsDefaultEndSearchIcon(
        hasExplicitSearchAction = searchAction != null,
        hasCustomEndSlot = endSlot != null,
    )
    PharmTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        accessibilityLabel = placeholder,
        enabled = enabled,
        imeAction = ImeAction.Search,
        onImeAction = onSearch,
        shape = t.shapes.pill,
        minHeight = pharmControlHeight,
        leadingSlot = null,
        trailingSlot = endSlot ?: searchAction?.let { search ->
            {
                PharmSearchAction(
                    onClick = search,
                    enabled = enabled && !searching,
                    searching = searching,
                )
            }
        } ?: if (showDefaultEndSearchIcon) {
            {
                Icon(
                    imageVector = PharmIcons.Search,
                    contentDescription = null,
                    tint = t.colors.fgMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        } else {
            null
        },
        trailingSlotAtEdge = endSlot == null && searchAction != null,
        onClear = { onValueChange("") },
        focusRequester = focusRequester,
        modifier = modifier,
    )
}

internal fun showsDefaultEndSearchIcon(
    hasExplicitSearchAction: Boolean,
    hasCustomEndSlot: Boolean,
): Boolean = !hasExplicitSearchAction && !hasCustomEndSlot

@Composable
fun PharmSearchAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    searching: Boolean = false,
) {
    val t = pharmTokens
    PharmIconButton(
        contentDescription = app.devper.pharm.ui.i18n.pharmStrings.commonSearch,
        onClick = onClick,
        enabled = enabled && !searching,
        minSize = t.dimens.minimumTouchTarget,
        shape = t.shapes.pill,
        modifier = modifier.size(t.dimens.minimumTouchTarget),
    ) {
        if (searching) {
            PharmCircularProgress(
                size = 16.dp,
                strokeWidth = 2.dp,
                color = t.colors.fg2,
            )
        } else {
            Icon(
                imageVector = PharmIcons.Search,
                contentDescription = null,
                tint = t.colors.fg2,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
