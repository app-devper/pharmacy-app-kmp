package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmModalSize { Sm, Md, Lg, Xl }

@Composable
fun PharmModal(
    open: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    size: PharmModalSize = PharmModalSize.Md,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (!open) return
    val t = pharmTokens
    val maxWidth: Dp = when (size) {
        PharmModalSize.Sm -> 384.dp
        PharmModalSize.Md -> 448.dp
        PharmModalSize.Lg -> 672.dp
        PharmModalSize.Xl -> 896.dp
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val shape = t.shapes.lg
        Column(
            modifier = modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape)
                .background(t.colors.surface, shape)
                .border(1.dp, t.colors.borderSubtle, shape),
        ) {
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = 9999.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(title, style = PharmText.h2)
                        if (subtitle != null) {
                            Text(subtitle, style = PharmText.meta)
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(t.shapes.sm),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "×",
                                style = PharmText.h1.copy(
                                    color = t.colors.fgMuted,
                                    textAlign = TextAlign.Center,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp)
                        .background(t.colors.divider)
                        .size(width = 9999.dp, height = 1.dp),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                content()
            }

            if (footer != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(t.colors.divider)
                        .size(width = 9999.dp, height = 1.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(t.colors.bgPage)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    footer()
                }
            }
        }
    }
}
