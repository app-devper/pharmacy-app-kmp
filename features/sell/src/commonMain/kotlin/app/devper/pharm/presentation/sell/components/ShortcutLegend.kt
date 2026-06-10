package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens


@Composable
fun ShortcutLegend(open: Boolean, onClose: () -> Unit) {
    val s = pharmStrings
    val shortcuts = listOf(
        "F1" to s.sellShortcutAll,
        "F2" to s.sellShortcutSearch,
        "F3" to s.sellShortcutCustomer,
        "F4" to s.sellShortcutCartDiscount,
        "F6" to s.sellShortcutPark,
        "F8" to s.sellShortcutParked,
        "F9" to s.sellShortcutPay,
        "Esc" to s.sellShortcutClose,
    )
    PharmModal(
        open = open,
        onDismiss = onClose,
        title = s.sellShortcuts,
        subtitle = s.sellShortcutSubtitle,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            shortcuts.forEach { (keys, description) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShortcutHint(label = keys, modifier = Modifier.width(64.dp))
                    Text(
                        text = description,
                        style = PharmText.body.copy(color = pharmTokens.colors.fg1),
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
        }
    }
}
