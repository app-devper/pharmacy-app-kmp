package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun KyFormHeader(form: KyFormType) {
    val t = pharmTokens
    val meta = kyFormMeta(form)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(t.shapes.lg)
                .background(meta.color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "ขย.${form.number}",
                style = PharmText.body.copy(color = Color.White, fontWeight = FontWeight.Bold),
            )
        }
        Column {
            Text(text = meta.title, style = PharmText.h1)
            Text(text = meta.subtitle, style = PharmText.meta)
        }
    }
}

internal val KyFormType.number: Int
    get() = when (this) {
        KyFormType.Ky9 -> 9
        KyFormType.Ky10 -> 10
        KyFormType.Ky11 -> 11
        KyFormType.Ky12 -> 12
    }

internal data class KyFormMeta(
    val color: Color,
    val title: String,
    val subtitle: String,
)

internal fun kyFormMeta(form: KyFormType): KyFormMeta = when (form) {
    KyFormType.Ky9 -> KyFormMeta(
        color = Color(0xFF2563EB),
        title = "ขย.9 — บัญชีการซื้อยา",
        subtitle = "บัญชีแสดงรายการซื้อยาและผลิตภัณฑ์สุขภาพ",
    )
    KyFormType.Ky10 -> KyFormMeta(
        color = Color(0xFF9333EA),
        title = "ขย.10 — บัญชียาควบคุมพิเศษ",
        subtitle = "บัญชีแสดงการขายยาควบคุมพิเศษ",
    )
    KyFormType.Ky11 -> KyFormMeta(
        color = Color(0xFFDC2626),
        title = "ขย.11 — บัญชียาอันตราย",
        subtitle = "บัญชีแสดงการขายยาอันตราย",
    )
    KyFormType.Ky12 -> KyFormMeta(
        color = Color(0xFF0D9488),
        title = "ขย.12 — บัญชียาที่ต้องใช้ใบสั่งแพทย์",
        subtitle = "บัญชีแสดงการขายยาที่ต้องใช้ใบสั่งแพทย์",
    )
}
