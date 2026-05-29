package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmFocusRing
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    hint: String? = null,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val labelText = if (required) {
            buildAnnotatedString {
                append(label)
                withStyle(SpanStyle(color = t.colors.dangerFg)) { append(" *") }
            }
        } else AnnotatedString(label)
        Text(text = labelText, style = PharmText.h3.copy(color = t.colors.fg2))

        content()

        when {
            error != null -> Text(
                text = error,
                style = PharmText.micro.copy(color = t.colors.dangerFg),
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                },
            )
            hint != null  -> Text(text = hint,  style = PharmText.micro)
        }
    }
}

@Composable
fun PharmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    isWarning: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingSlot: (@Composable () -> Unit)? = null,
    trailingSlot: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val isFocused by interaction.collectIsFocusedAsState()
    val borderColor = when {
        !enabled   -> t.colors.borderSubtle
        isError    -> t.colors.dangerFg
        isWarning  -> t.colors.warningFg
        isFocused  -> t.colors.accent
        else       -> t.colors.border
    }
    val borderThickness = if (isFocused && enabled) 1.5.dp else 1.dp
    val bg = when {
        !enabled  -> t.colors.bgPage
        isWarning -> t.colors.warningBg.copy(alpha = 0.6f)
        else      -> t.colors.surface
    }
    val shape = t.shapes.md
    val style = PharmText.body.copy(color = if (enabled) t.colors.fg1 else t.colors.fgMuted)

    val selectionColors = TextSelectionColors(
        handleColor = t.colors.accent,
        backgroundColor = t.colors.accent.copy(alpha = 0.25f),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pharmFocusRing(interactionSource = interaction, shape = shape)
            .padding(2.dp)
            .clip(shape)
            .background(bg, shape)
            .border(borderThickness, borderColor, shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingSlot != null) {
            leadingSlot()
        }
        Box(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    textStyle = style,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                    keyboardActions = if (onImeAction != null) KeyboardActions(
                        onDone = { onImeAction() },
                        onSearch = { onImeAction() },
                        onGo = { onImeAction() },
                        onSend = { onImeAction() },
                    ) else KeyboardActions.Default,
                    visualTransformation = visualTransformation,
                    interactionSource = interaction,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(t.colors.accent),
                    decorationBox = { inner ->
                        Box {
                            if (value.isEmpty() && placeholder != null) {
                                Text(
                                    text = placeholder,
                                    style = style.copy(color = t.colors.fgMuted),
                                )
                            }
                            inner()
                        }
                    },
                )
            }
        }
        Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
            if (isError) {
                Icon(
                    imageVector = PharmIcons.AlertCircle,
                    contentDescription = "ข้อผิดพลาด",
                    tint = t.colors.dangerFg,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        if (trailingSlot != null) {
            trailingSlot()
        }
    }
}
