@file:OptIn(ExperimentalWasmJsInterop::class)

package app.devper.pharm.platform

import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLIFrameElement

class ReceiptPrinterImpl : ReceiptPrinter {
    override fun print(template: ReceiptTemplate): Boolean {
        val iframe = document.createElement("iframe") as HTMLIFrameElement
        iframe.style.position = "fixed"
        iframe.style.right = "0"
        iframe.style.bottom = "0"
        iframe.style.width = "0"
        iframe.style.height = "0"
        iframe.style.border = "0"
        document.body?.appendChild(iframe)

        val html = buildHtml(template)
        val cw = iframe.contentWindow ?: run {
            document.body?.removeChild(iframe)
            return false
        }
        val cd = cw.document
        cd.open()
        cd.write(html)
        cd.close()

        window.setTimeout({
            runCatching { cw.focus(); cw.print() }
            null
        }, 50)
        return true
    }
}

private fun buildHtml(t: ReceiptTemplate): String {
    val itemsHtml = t.items.joinToString("") { line ->
        """<tr>
            <td>${escape(line.name)}<br><span class="muted">  ${line.displayQty} ${escape(line.displayUnit)} × ${formatBaht(line.unitPrice)}</span></td>
            <td class="right">${formatBaht(line.lineTotal)}</td>
        </tr>""".trimIndent()
    }
    val itemDisc = if (t.itemDiscountTotal > 0) {
        """<tr><td>ส่วนลดรายการ</td><td class="right">-${formatBaht(t.itemDiscountTotal)}</td></tr>"""
    } else ""
    val cartDisc = if (t.cartDiscount > 0) {
        """<tr><td>ส่วนลดบิล</td><td class="right">-${formatBaht(t.cartDiscount)}</td></tr>"""
    } else ""
    val customer = if (t.customerName.isNotBlank()) {
        """<div>ลูกค้า: ${escape(t.customerName)}</div>"""
    } else ""
    val pharmacist = if (t.pharmacistName.isNotBlank()) {
        """<div class="muted">เภสัชกร: ${escape(t.pharmacistName)}</div>"""
    } else ""
    return """
<!doctype html>
<html lang="th">
<head>
<meta charset="utf-8" />
<title>${escape(t.billNo)}</title>
<style>
  body { font-family: 'Sarabun', monospace; font-size: 12px; margin: 12px; }
  h1 { font-size: 14px; margin: 0 0 4px 0; text-align: center; }
  .muted { color: #555; font-size: 11px; }
  table { width: 100%; border-collapse: collapse; }
  td { padding: 2px 0; vertical-align: top; }
  .right { text-align: right; }
  .header, .footer { text-align: center; }
  .total { font-weight: bold; }
  hr { border: none; border-top: 1px dashed #999; margin: 6px 0; }
  @media print { body { margin: 0; } }
</style>
</head>
<body>
  <div class="header">
    <h1>${escape(t.storeName)}</h1>
    <div class="muted">${escape(t.storeAddress)}</div>
    <div class="muted">โทร ${escape(t.storePhone)}${if (t.storeTaxId.isNotBlank()) " · เลขผู้เสียภาษี ${escape(t.storeTaxId)}" else ""}</div>
  </div>
  <hr />
  <div>เลขบิล: <b>${escape(t.billNo)}</b></div>
  <div>วันที่: ${escape(t.soldAt)}</div>
  $customer
  <hr />
  <table>$itemsHtml</table>
  <hr />
  <table>
    $itemDisc
    $cartDisc
    <tr class="total"><td>รวมสุทธิ</td><td class="right">${formatBaht(t.total)}</td></tr>
    <tr><td>รับเงิน</td><td class="right">${formatBaht(t.received)}</td></tr>
    <tr><td>เงินทอน</td><td class="right">${formatBaht(t.change)}</td></tr>
  </table>
  <hr />
  $pharmacist
  <div class="footer muted">${escape(t.footer)}</div>
</body>
</html>
""".trimIndent()
}

private fun escape(s: String): String = s
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")

private fun formatBaht(value: Double): String {
    val cents = (value * 100.0 + if (value >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).toString().padStart(2, '0')
    return "$whole.$frac"
}
