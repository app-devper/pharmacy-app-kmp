package app.devper.pharm.platform

import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import platform.Foundation.NSLog

class ReceiptPrinterImpl : ReceiptPrinter {
    override fun print(template: ReceiptTemplate): Boolean {
        NSLog("ReceiptPrinter: print() not implemented on iOS (bill ${template.billNo})")
        return false
    }
}