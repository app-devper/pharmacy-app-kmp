package app.devper.pharm.di

import app.devper.pharm.presentation.bulkimport.BulkImportViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val bulkImportModule = module {
    factoryOf(::BulkImportViewModel)
}
