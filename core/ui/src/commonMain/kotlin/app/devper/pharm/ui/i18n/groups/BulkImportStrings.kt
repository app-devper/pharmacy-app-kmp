package app.devper.pharm.ui.i18n.groups

interface BulkImportStrings {
    val bulkImportReadyBadge: String

    val bulkImportPasteFirst: String
    val bulkImportNotArray: String
    val bulkImportRowNotObject: (Int) -> String
    val bulkImportRowMissingName: String

    val bulkImportPickFileFailed: String
    val bulkImportImportFailed: String
    val bulkImportNoRows: String
    val bulkImportInvalidJson: String

    val bulkImportTitle: String
    val bulkImportSubtitle: String
    val bulkImportDropZoneHint: String
    val bulkImportDropZonePickFile: String
    val bulkImportSupportsHint: String
    val bulkImportPasteHere: String
    val bulkImportPasteHint: String
    val bulkImportDownloadTemplate: String
    val bulkImportValidateCta: String
    val bulkImportValidatePromptHint: String
    val bulkImportValidatedReady: (Int) -> String
    val bulkImportImportAllCta: String
    val bulkImportEmptyDropped: String
    val bulkImportEmptyDefault: String
    val bulkImportHeaderGeneric: String
    val bulkImportStatusReady: String
    val bulkImportStatusError: String
    val bulkImportResultTitle: (Int) -> String
    val bulkImportResultAllSuccess: String
    val bulkImportResultPartial: String
    val bulkImportResultAllFail: String
    val bulkImportResultSummary: (Int, Int) -> String
    val bulkImportResultSuccessLabel: String
    val bulkImportClearCta: String
}
