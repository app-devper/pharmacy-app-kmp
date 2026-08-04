package app.devper.pharm.presentation.help

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.help.i18n.localize
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.help.MarkdownText
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import androidx.compose.material3.Icon
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar

private const val DUAL_PANE_MIN_DP = 720
private const val PINNED_ITEMS_BEFORE_SECTIONS = 1

@Composable
fun HelpContent(
    state: HelpUiState,
    callbacks: HelpCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = s.navHelp,
            subtitle = s.helpSubtitle,
            compactTopbarActions = true,
            actions = {
                PharmButton(
                    label = s.commonRefresh,
                    onClick = callbacks.onReload,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Sm,
                    loading = state.loading,
                    leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
                )
            },
        )
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when {
                state.loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { PharmCircularProgress(color = t.colors.accent) }

                state.markdown.isBlank() -> PharmEmptyState(
                    title = s.helpNotFound,
                    icon = PharmIcons.Help,
                    action = {
                        PharmButton(
                            label = s.commonRetry,
                            onClick = callbacks.onReload,
                            size = PharmButtonSize.Sm,
                        )
                    },
                )

                else -> HelpBody(state = state)
            }
        }
    }
    ErrorBottomSheet(message = state.errorState?.localize(s), onDismiss = callbacks.onDismissError)
}

@Composable
private fun HelpBody(
    state: HelpUiState,
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
                        .widthIn(max = pharmTokens.dimens.readingContentMaxWidth)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                HelpCompactToc(
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
        item("__tip__") { KeyboardTipBanner() }
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
