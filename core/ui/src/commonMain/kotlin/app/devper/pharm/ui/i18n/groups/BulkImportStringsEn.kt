package app.devper.pharm.ui.i18n.groups

object BulkImportStringsEn : BulkImportStrings {
    override val bulkImportReadyBadge = "Ready to import"

    override val bulkImportTitle = "Bulk import drugs (JSON)"
    override val bulkImportSubtitle = "Upload a JSON file or paste text to create many drugs at once"
    override val bulkImportDropZoneHint = "Drop a JSON file here or pick one"
    override val bulkImportDropZonePickFile = "Pick file"
    override val bulkImportSupportsHint = "Accepts an array or {\"drugs\": [...]} — up to 1,000 items"
    override val bulkImportPasteHere = "Or paste JSON here"
    override val bulkImportPasteHint = "Accepts either an array or {\"drugs\": [...]}"
    override val bulkImportDownloadTemplate = "Download template"
    override val bulkImportValidateCta = "Validate"
    override val bulkImportValidatePromptHint = "Validate JSON first"
    override val bulkImportValidatedReady: (Int) -> String = { count -> "Validated — $count item(s) ready to import" }
    override val bulkImportImportAllCta = "Import all"
    override val bulkImportEmptyDropped = "No items to import"
    override val bulkImportEmptyDefault = "No items yet"
    override val bulkImportHeaderGeneric = "Generic"
    override val bulkImportStatusReady = "Ready"
    override val bulkImportStatusError = "Error"
    override val bulkImportResultTitle: (Int) -> String = { count -> "Import result · $count item(s)" }
    override val bulkImportResultAllSuccess = "Imported all"
    override val bulkImportResultPartial = "Partially imported"
    override val bulkImportResultAllFail = "Import failed"
    override val bulkImportResultSummary: (Int, Int) -> String = { imported, total -> "Recorded $imported/$total item(s)" }
    override val bulkImportResultSuccessLabel = "Success"
    override val bulkImportClearCta = "Clear"
    override val bulkImportPickFileFailed = "Failed to pick file"
    override val bulkImportImportFailed = "Import failed"
    override val bulkImportNoRows = "No items to import"
    override val bulkImportInvalidJson = "Invalid JSON format"
    override val bulkImportPasteFirst = "Paste JSON before validating"
    override val bulkImportNotArray = "Must be an array or {drugs: [...]}"
    override val bulkImportRowNotObject: (Int) -> String = { row -> "Row $row: must be a JSON object" }
    override val bulkImportRowMissingName = "Every row needs a name field"
}
