package app.devper.pharm.common.print

interface ReceiptPrinter {
    fun print(template: ReceiptTemplate): Boolean
}
