package app.devper.pharm.di

import app.devper.pharm.presentation.bulkimport.BulkImportViewModel
import app.devper.pharm.presentation.imports.ImportDetailViewModel
import app.devper.pharm.presentation.imports.ImportFormViewModel
import app.devper.pharm.presentation.imports.ImportsListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val purchaseOrdersModule = module {
    factoryOf(::ImportsListViewModel)
    factoryOf(::ImportFormViewModel)
    factoryOf(::ImportDetailViewModel)
    factoryOf(::BulkImportViewModel)
}
