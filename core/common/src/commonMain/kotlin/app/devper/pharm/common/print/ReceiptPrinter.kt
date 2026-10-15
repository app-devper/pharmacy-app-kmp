package app.devper.pharm.common.print

interface ReceiptPrinter {
    suspend fun print(template: ReceiptTemplate): Boolean
}
