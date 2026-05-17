package app.devper.pharm.platform

import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterException
import java.awt.print.PrinterJob

class ReceiptPrinterImpl : ReceiptPrinter {
    override fun print(template: ReceiptTemplate): Boolean {
        val job = PrinterJob.getPrinterJob()
        job.setPrintable(ReceiptPrintable(template))
        if (!job.printDialog()) return false
        return try {
            job.print()
            true
        } catch (e: PrinterException) {
            println("ReceiptPrinter: print failed: ${e.message}")
            false
        }
    }
}

private class ReceiptPrintable(private val t: ReceiptTemplate) : Printable {
    override fun print(g: Graphics, pf: PageFormat, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE
        val g2 = g as Graphics2D
        g2.translate(pf.imageableX, pf.imageableY)
        g2.font = Font(Font.MONOSPACED, Font.PLAIN, 11)

        val lineGap = 14
        var y = 16

        fun line(text: String, bold: Boolean = false) {
            val prev = g2.font
            g2.font = Font(Font.MONOSPACED, if (bold) Font.BOLD else Font.PLAIN, 11)
            g2.drawString(text, 0, y)
            g2.font = prev
            y += lineGap
        }

        line(t.storeName, bold = true)
        if (t.storeAddress.isNotBlank()) line(t.storeAddress)
        if (t.storePhone.isNotBlank()) line("โทร ${t.storePhone}")
        if (t.storeTaxId.isNotBlank()) line("เลขผู้เสียภาษี ${t.storeTaxId}")
        y += 4

        line("------------------------------------------")
        line("เลขบิล: ${t.billNo}")
        line("วันที่:  ${t.soldAt}")
        if (t.customerName.isNotBlank()) line("ลูกค้า: ${t.customerName}")
        line("------------------------------------------")

        for (item in t.items) {
            line(item.name)
            val qtyLine = "  ${item.displayQty} ${item.displayUnit} x ${formatBaht(item.unitPrice)}"
            val totalRight = formatBaht(item.lineTotal).padStart(10, ' ')
            line(qtyLine.padEnd(32, ' ') + totalRight)
        }
        line("------------------------------------------")

        if (t.itemDiscountTotal > 0) line("ส่วนลดรายการ".padEnd(32, ' ') + ("-" + formatBaht(t.itemDiscountTotal)).padStart(10, ' '))
        if (t.cartDiscount > 0) line("ส่วนลดบิล".padEnd(32, ' ') + ("-" + formatBaht(t.cartDiscount)).padStart(10, ' '))
        line("รวมสุทธิ".padEnd(32, ' ') + formatBaht(t.total).padStart(10, ' '), bold = true)
        line("รับเงิน".padEnd(32, ' ') + formatBaht(t.received).padStart(10, ' '))
        line("เงินทอน".padEnd(32, ' ') + formatBaht(t.change).padStart(10, ' '))

        y += 4
        if (t.pharmacistName.isNotBlank()) line("เภสัชกร: ${t.pharmacistName}")
        if (t.footer.isNotBlank()) line(t.footer)

        return Printable.PAGE_EXISTS
    }
}

private fun formatBaht(value: Double): String {
    val cents = (value * 100.0 + if (value >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).toString().padStart(2, '0')
    return "$whole.$frac"
}
