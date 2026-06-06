package app.devper.pharm.presentation.help

import app.devper.pharm.features.help.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class ResMarkdownLoader : MarkdownLoader {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun loadUserGuide(): String =
        Res.readBytes("files/user_guide.md").decodeToString()
}
