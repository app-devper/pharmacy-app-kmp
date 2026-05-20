package app.devper.pharm.ui.print

import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.Settings

fun buildEodReceiptTemplate(
    closed: EodCloseResult,
    settings: Settings,
): ReceiptTemplate {
    val report = closed.report
    val closedByLine = if (closed.closedBy.isNotBlank()) "ผู้ปิดรอบ: ${closed.closedBy}" else ""
    val footer = "เงินเข้าลิ้นชัก ${formatBaht(report.netCash)} · ${report.billCount} บิล"
    return ReceiptTemplate(
        storeName = settings.store.name,
        storeAddress = settings.store.address,
        storePhone = settings.store.phone,
        storeTaxId = settings.store.taxId,
        billNo = "EOD-${closed.date.ifBlank { report.date }}",
        soldAt = closed.closedAt,
        customerName = closedByLine,
        items = emptyList(),
        subtotal = report.totalSales,
        itemDiscountTotal = 0.0,
        cartDiscount = report.totalDiscount,
        total = report.totalSales,
        received = report.totalReceived,
        change = report.totalChange,
        pharmacistName = settings.pharmacist.name,
        footer = footer,
    )
}

private fun formatBaht(value: Double): String {
    val cents = (value * 100.0 + if (value >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).toString().padStart(2, '0')
    return "$whole.$frac"
}
