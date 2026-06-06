package app.devper.pharm.di

import app.devper.pharm.presentation.help.HelpViewModel
import app.devper.pharm.presentation.help.MarkdownLoader
import app.devper.pharm.presentation.help.ResMarkdownLoader
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val helpModule = module {
    singleOf(::ResMarkdownLoader) bind MarkdownLoader::class
    factoryOf(::HelpViewModel)
}
