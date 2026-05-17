package app.devper.pharm.platform

import android.util.Log
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate

class ReceiptPrinterImpl : ReceiptPrinter {
    override fun print(template: ReceiptTemplate): Boolean {
        Log.w("ReceiptPrinter", "print() not implemented on Android (bill ${template.billNo})")
        return false
    }
}