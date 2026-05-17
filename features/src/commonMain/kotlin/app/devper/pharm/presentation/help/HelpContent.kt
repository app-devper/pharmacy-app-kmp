package app.devper.pharm.presentation.help

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.help.MarkdownText
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

data class HelpSection(
    val id: String,
    val title: String,
    val markdown: String,
)

private const val DUAL_PANE_MIN_DP = 720
private const val PINNED_ITEMS_BEFORE_SECTIONS = 2

@Composable
fun HelpContent(
    state: HelpUiState,
    callbacks: HelpCallbacks,
) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        when {
            state.loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = t.colors.accent) }

            state.markdown.isBlank() -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "ไม่พบคู่มือ",
                    style = PharmText.body.copy(color = t.colors.fg3),
                )
            }

            else -> HelpBody(state = state, callbacks = callbacks)
        }
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun HelpBody(
    state: HelpUiState,
    callbacks: HelpCallbacks,
) {
    val sections = remember(state.markdown) { splitSections(state.markdown) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val activeId by remember(sections) {
        derivedStateOf {
            if (sections.isEmpty()) return@derivedStateOf null
            val sectionIdx = (listState.firstVisibleItemIndex - PINNED_ITEMS_BEFORE_SECTIONS)
                .coerceAtLeast(0)
                .coerceAtMost(sections.lastIndex)
            sections[sectionIdx].id
        }
    }

    val onSelect: (String) -> Unit = { id ->
        callbacks.onTocClick(id)
        val idx = sections.indexOfFirst { it.id == id }
        if (idx >= 0) {
            scope.launch { listState.animateScrollToItem(idx + PINNED_ITEMS_BEFORE_SECTIONS) }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDualPane = maxWidth.value >= DUAL_PANE_MIN_DP
        if (isDualPane) {
            Row(modifier = Modifier.fillMaxSize()) {
                HelpToc(
                    sections = sections,
                    activeId = activeId,
                    onSelect = onSelect,
                    modifier = Modifier
                        .width(240.dp)
                        .fillMaxSize()
                        .padding(start = 24.dp, top = 24.dp, end = 8.dp, bottom = 24.dp)
                        .verticalScroll(rememberScrollState()),
                )
                HelpArticle(
                    sections = sections,
                    listState = listState,
                    modifier = Modifier
                        .widthIn(max = 880.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                HelpToc(
                    sections = sections,
                    activeId = activeId,
                    onSelect = onSelect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                )
                HelpArticle(
                    sections = sections,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun HelpToc(
    sections: List<HelpSection>,
    activeId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "สารบัญ",
            style = PharmText.thead.copy(color = t.colors.fg3),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        sections.forEach { section ->
            val isActive = section.id == activeId
            val fg = if (isActive) t.colors.accent else t.colors.fg2
            val bg = if (isActive) t.colors.accentBgSoft else t.colors.bgPage
            Text(
                text = section.title,
                style = PharmText.bodySm.copy(
                    color = fg,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(t.shapes.md)
                    .background(bg)
                    .clickable { onSelect(section.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun HelpArticle(
    sections: List<HelpSection>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item("__title__") {
            ArticleHeader()
        }
        item("__tip__") {
            KeyboardTipBanner()
        }
        items(
            count = sections.size,
            key = { idx -> sections[idx].id },
        ) { idx ->
            val section = sections[idx]
            Column(modifier = Modifier.fillMaxWidth()) {
                MarkdownText(markdown = section.markdown)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ArticleHeader() {
    val t = pharmTokens
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = "คู่มือการใช้งาน", style = PharmText.h1)
        Text(
            text = "ระบบ POS ร้านขายยา · เลือกหัวข้อจากสารบัญทางซ้าย",
            style = PharmText.meta.copy(color = t.colors.fgMuted),
        )
    }
}

@Composable
private fun KeyboardTipBanner() {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.infoBg)
            .border(1.dp, t.colors.infoFg.copy(alpha = 0.25f), t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "เคล็ดลับ",
            style = PharmText.bodySm.copy(
                color = t.colors.infoFg,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        KeyboardKey("F1")
        Text(
            text = "โฟกัสช่องค้นหา",
            style = PharmText.bodySm.copy(color = t.colors.infoFg),
        )
        KeyboardKey("F2")
        Text(
            text = "ช่องรับเงิน",
            style = PharmText.bodySm.copy(color = t.colors.infoFg),
        )
        KeyboardKey("F4")
        Text(
            text = "พักบิล",
            style = PharmText.bodySm.copy(color = t.colors.infoFg),
        )
    }
}

@Composable
private fun KeyboardKey(label: String) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(t.colors.surface)
            .border(1.dp, t.colors.infoFg.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = label,
            style = PharmText.micro.copy(
                color = t.colors.infoFg,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

private val H2_HEADING = Regex("(?m)^## (?!#)(.+)$")

internal fun splitSections(markdown: String): List<HelpSection> {
    val normalized = markdown.replace("\r\n", "\n")
    val matches = H2_HEADING.findAll(normalized).toList()
    if (matches.isEmpty()) return emptyList()
    return matches.mapIndexed { index, match ->
        val title = match.groupValues[1].trim()
        val from = match.range.first
        val to = matches.getOrNull(index + 1)?.range?.first ?: normalized.length
        HelpSection(
            id = slugify(title),
            title = title,
            markdown = normalized.substring(from, to).trimEnd('\n'),
        )
    }
}

private fun slugify(text: String): String {
    val builder = StringBuilder()
    for (ch in text) {
        when {
            ch.isLetterOrDigit() -> builder.append(ch.lowercaseChar())
            ch == ' ' || ch == '-' || ch == '_' -> builder.append('-')
            else -> Unit
        }
    }
    val raw = builder.toString().trim('-').replace(Regex("-+"), "-")
    return raw.ifBlank { "section-${text.hashCode().toString(16)}" }
}

private val PreviewMarkdown = """
# คู่มือการใช้งาน

## เริ่มต้นใช้งาน

ลงชื่อเข้าใช้ด้วย username และ password

## หน้าขาย — Flow พื้นฐาน

- ค้นหายา / สแกนบาร์โค้ด
- เพิ่มลงตะกร้า
- เลือกลูกค้า
- รับเงิน + ออกใบเสร็จ

## สต็อก และล็อต

จัดการล็อต FEFO และ Alt-units

## ขย. 9–12

ฟอร์มยาควบคุม

## รายงาน

ยอดขาย / กำไร / EOD

## โหมดออฟไลน์

คิวค้างซิงค์ และปัญหาที่พบบ่อย
""".trimIndent()

@Preview
@Composable
private fun HelpContent_Loaded_Preview() {
    PharmacyTheme {
        HelpContent(
            state = HelpUiState(markdown = PreviewMarkdown),
            callbacks = HelpCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun HelpContent_Loading_Preview() {
    PharmacyTheme {
        HelpContent(
            state = HelpUiState(loading = true),
            callbacks = HelpCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun HelpContent_Empty_Preview() {
    PharmacyTheme {
        HelpContent(
            state = HelpUiState(markdown = ""),
            callbacks = HelpCallbacks.Preview,
        )
    }
}
