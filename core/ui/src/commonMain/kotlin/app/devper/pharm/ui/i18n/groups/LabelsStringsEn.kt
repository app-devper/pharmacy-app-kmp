package app.devper.pharm.ui.i18n.groups

object LabelsStringsEn : LabelsStrings {
    override val labelsAddCta = "+ Add"

    override val labelsLoadDrugsFailed = "Failed to load drugs for labels"
    override val labelsSubtitle = "Design and print drug labels"
    override val labelsSearchPlaceholder = "Search drugs (add one per line)…"
    override val labelsNoDrugs = "No drugs available"
    override val labelsNoSearchResults = "No matching drugs"
    override val labelsEmpty = "No items yet — pick drugs on the left to add"
    override val labelsListTitle: (Int) -> String = { count -> "Label list ($count line(s))" }
    override val labelsRemoveLine = "Remove line"
    override val labelsClear = "Clear"
    override val labelsClearTitle = "Clear label list?"
    override val labelsClearSubtitle = "All prepared label lines will be removed."
    override val labelsClearConfirm = "Clear list"
    override val labelsSizeLabel = "Label size"
    override val labelsSizeSmall = "38 × 25 mm"
    override val labelsSizeMedium = "50 × 30 mm"
    override val labelsPreviewLabel: (String) -> String = { size -> "Preview ($size)" }
    override val labelsPrintCount: (Int) -> String = { count -> "Print $count label(s)" }
    override val labelsPrinting = "Printing…"
    override val labelsPrintSuccess = "Printed"
    override val labelsTotalPrice = "Total price"
    override val labelsLotPrefix = "Lot"
    override val labelsLotUnspecified = "(unspecified)"
    override val labelsPrintFailed = "Printing failed"
}
