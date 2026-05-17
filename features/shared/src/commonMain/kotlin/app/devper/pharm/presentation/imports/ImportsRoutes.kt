package app.devper.pharm.presentation.imports

import kotlinx.serialization.Serializable

@Serializable data object Imports
@Serializable data object ImportNew
@Serializable data class ImportEdit(val id: String)
@Serializable data class ImportDetail(val id: String)
