package app.devper.pharm.presentation.help

interface MarkdownLoader {
    suspend fun loadUserGuide(): String
}
