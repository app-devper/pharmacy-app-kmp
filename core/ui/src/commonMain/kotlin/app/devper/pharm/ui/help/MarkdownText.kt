package app.devper.pharm.ui.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val blocks = remember(markdown) { parseBlocks(markdown) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block -> RenderBlock(block) }
    }
}

private sealed interface MdBlock {
    data class Heading(val level: Int, val text: String) : MdBlock
    data class Paragraph(val text: String) : MdBlock
    data class Bullet(val items: List<String>) : MdBlock
    data class Quote(val text: String) : MdBlock
    data class TableBlock(val header: List<String>, val rows: List<List<String>>) : MdBlock
    data object HorizontalRule : MdBlock
}

private fun parseBlocks(md: String): List<MdBlock> {
    val lines = md.replace("\r\n", "\n").split('\n')
    val blocks = mutableListOf<MdBlock>()
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        when {
            line.isBlank() -> { i++ }

            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length.coerceAtMost(4)
                val text = line.drop(level).trim()
                blocks += MdBlock.Heading(level, text)
                i++
            }

            line.trim() == "---" || line.trim() == "***" -> {
                blocks += MdBlock.HorizontalRule
                i++
            }

            line.startsWith("> ") -> {
                blocks += MdBlock.Quote(line.removePrefix("> ").trim())
                i++
            }

            line.startsWith("- ") -> {
                val items = mutableListOf<String>()
                while (i < lines.size && lines[i].startsWith("- ")) {
                    items += lines[i].removePrefix("- ").trim()
                    i++
                }
                blocks += MdBlock.Bullet(items)
            }

            line.startsWith("|") -> {
                val tableLines = mutableListOf<String>()
                while (i < lines.size && lines[i].startsWith("|")) {
                    tableLines += lines[i]
                    i++
                }
                blocks += parseTable(tableLines)
            }

            else -> {

                val sb = StringBuilder(line)
                i++
                while (i < lines.size) {
                    val next = lines[i]
                    if (next.isBlank() || next.startsWith("#") || next.startsWith("- ") ||
                        next.startsWith("> ") || next.startsWith("|") || next.trim() == "---"
                    ) break
                    sb.append(' ').append(next.trim())
                    i++
                }
                blocks += MdBlock.Paragraph(sb.toString())
            }
        }
    }
    return blocks
}

private fun parseTable(rawLines: List<String>): MdBlock.TableBlock {
    if (rawLines.size < 2) return MdBlock.TableBlock(emptyList(), emptyList())
    fun split(row: String): List<String> = row
        .removePrefix("|").removeSuffix("|")
        .split("|")
        .map { it.trim() }
    val header = split(rawLines[0])

    val rows = rawLines.drop(2).map(::split)
    return MdBlock.TableBlock(header = header, rows = rows)
}

@Composable
private fun RenderBlock(block: MdBlock) {
    when (block) {
        is MdBlock.Heading -> {
            val style = when (block.level) {
                1 -> PharmText.h1
                2 -> PharmText.h2
                else -> PharmText.h3
            }
            Text(
                text = renderInline(block.text),
                style = style,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = if (block.level <= 2) 12.dp else 4.dp, bottom = 2.dp),
            )
        }

        is MdBlock.Paragraph -> Text(
            text = renderInline(block.text),
            style = PharmText.body,
        )

        is MdBlock.Bullet -> Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            block.items.forEach { item ->
                Row {
                    Text(
                        text = "•  ",
                        style = PharmText.body.copy(color = pharmTokens.colors.accent),
                    )
                    Text(
                        text = renderInline(item),
                        style = PharmText.body,
                    )
                }
            }
        }

        is MdBlock.Quote -> Box(
            modifier = Modifier
                .clip(pharmTokens.shapes.md)
                .background(pharmTokens.colors.surfaceRaised),
        ) {
            Text(
                text = renderInline(block.text),
                style = PharmText.body.copy(color = pharmTokens.colors.fg2),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        is MdBlock.TableBlock -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(pharmTokens.shapes.md)
                .background(pharmTokens.colors.surfaceRaised),
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (block.header.isNotEmpty()) {
                    Row {
                        block.header.forEach { cell ->
                            Text(
                                text = renderInline(cell),
                                style = PharmText.thead,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                            )
                        }
                    }
                    MarkdownDivider()
                }
                block.rows.forEach { row ->
                    Row {
                        row.forEach { cell ->
                            Text(
                                text = renderInline(cell),
                                style = PharmText.bodySm,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp, vertical = 3.dp),
                            )
                        }
                    }
                }
            }
        }

        MdBlock.HorizontalRule -> MarkdownDivider(modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
private fun renderInline(text: String): AnnotatedString = buildAnnotatedString {
    val primary = pharmTokens.colors.accent
    var i = 0
    while (i < text.length) {
        when {

            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end > 0) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
            }

            text[i] == '*' && (i + 1 < text.length && text[i + 1] != '*') -> {
                val end = text.indexOf('*', i + 1)
                if (end > 0) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else { append(text[i]); i++ }
            }

            text[i] == '`' -> {
                val end = text.indexOf('`', i + 1)
                if (end > 0) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Medium,
                            background = primary.copy(alpha = 0.08f),
                        ),
                    ) { append(text.substring(i + 1, end)) }
                    i = end + 1
                } else { append(text[i]); i++ }
            }

            text[i] == '[' -> {
                val close = text.indexOf(']', i + 1)
                if (close > 0 && close + 1 < text.length && text[close + 1] == '(') {
                    val end = text.indexOf(')', close + 2)
                    if (end > 0) {
                        withStyle(
                            SpanStyle(color = primary, textDecoration = TextDecoration.Underline),
                        ) { append(text.substring(i + 1, close)) }
                        i = end + 1
                    } else { append(text[i]); i++ }
                } else { append(text[i]); i++ }
            }

            else -> { append(text[i]); i++ }
        }
    }
}

@Composable
private fun MarkdownDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(pharmTokens.colors.divider),
    )
}
