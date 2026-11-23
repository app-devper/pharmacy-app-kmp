package app.devper.pharm.ui.print

import app.devper.pharm.common.print.ReceiptLine
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.PricedBill
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings

fun buildReceiptTemplate(
    sale: Sale,
    bill: PricedBill,
    lines: List<CartLine>,
    customer: Customer?,
    settings: Settings,
    received: Double,
    soldAtFormatted: String,
): ReceiptTemplate {
    return ReceiptTemplate(
        storeName = settings.store.name,
        storeAddress = settings.store.address,
        storePhone = settings.store.phone,
        storeTaxId = settings.store.taxId,
        billNo = sale.billNo,
        soldAt = soldAtFormatted,
        customerName = customer?.name.orEmpty(),
        items = lines.map { line ->
            ReceiptLine(
                name = line.drug.name,
                displayQty = line.displayQty,
                displayUnit = line.displayUnit,
                unitPrice = line.unitPrice.amount,
                lineTotal = line.lineTotal.amount,
            )
        },
        subtotal = bill.grossSubtotal.amount,
        itemDiscountTotal = bill.itemDiscountTotal.amount,
        cartDiscount = sale.discount.amount,
        total = sale.total.amount,
        received = received,
        change = sale.change.amount,
        pharmacistName = settings.pharmacist.name,
        footer = "ขอบคุณที่ใช้บริการ",
    )
}
