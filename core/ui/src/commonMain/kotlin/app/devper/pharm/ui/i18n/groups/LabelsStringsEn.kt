package app.devper.pharm.ui.i18n.groups

object LabelsStringsEn : LabelsStrings {
    override val labelsAddCta = "+ Add"

    override val labelsSubtitle = "Design and print drug labels"
    override val labelsSearchPlaceholder = "Search drugs (add one per line)…"
    override val labelsEmpty = "No items yet — pick drugs on the left to add"
    override val labelsListTitle: (Int) -> String = { count -> "Label list ($count line(s))" }
    override val labelsRemoveLine = "Remove line"
    override val labelsClear = "Clear"
    override val labelsSizeLabel = "Label size"
    override val labelsPreviewLabel: (String) -> String = { size -> "Preview ($size)" }
    override val labelsPrintCount: (Int) -> String = { count -> "Print $count label(s)" }
    override val labelsPrinting = "Printing…"
    override val labelsPrintSuccess = "Printed"
    override val labelsTotalPrice = "Total price"
    override val labelsLotPrefix = "Lot"
    override val labelsLotUnspecified = "(unspecified)"
    override val labelsPrintFailed = "Printing failed"
}
