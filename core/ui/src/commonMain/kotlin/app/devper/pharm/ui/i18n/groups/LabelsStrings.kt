package app.devper.pharm.ui.i18n.groups

interface LabelsStrings {
    val labelsAddCta: String

    val labelsPrintFailed: String
    val labelsSubtitle: String
    val labelsSearchPlaceholder: String
    val labelsNoDrugs: String
    val labelsNoSearchResults: String
    val labelsEmpty: String
    val labelsListTitle: (Int) -> String
    val labelsRemoveLine: String
    val labelsClear: String
    val labelsClearTitle: String
    val labelsClearSubtitle: String
    val labelsClearConfirm: String
    val labelsSizeLabel: String
    val labelsSizeSmall: String
    val labelsSizeMedium: String
    val labelsPreviewLabel: (String) -> String
    val labelsPrintCount: (Int) -> String
    val labelsPrinting: String
    val labelsPrintSuccess: String
    val labelsTotalPrice: String
    val labelsLotPrefix: String
    val labelsLotUnspecified: String
}
