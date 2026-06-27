package app.devper.pharm.presentation.help

data class HelpSection(
    val id: String,
    val title: String,
    val markdown: String,
)

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
