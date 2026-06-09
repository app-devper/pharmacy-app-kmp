package app.devper.pharm.ui.i18n.groups

interface LabelsStrings {
    val labelsPrintFailed: String
    val labelsSubtitle: String
    val labelsSearchPlaceholder: String
    val labelsEmpty: String
    val labelsListTitle: (Int) -> String
    val labelsRemoveLine: String
    val labelsClear: String
    val labelsSizeLabel: String
    val labelsPreviewLabel: (String) -> String
    val labelsPrintCount: (Int) -> String
    val labelsPrinting: String
    val labelsPrintSuccess: String
    val labelsTotalPrice: String
    val labelsLotPrefix: String
    val labelsLotUnspecified: String
}
