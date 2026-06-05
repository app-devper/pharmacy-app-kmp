package app.devper.pharm.ui.print

import app.devper.pharm.common.print.ReceiptLine
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings

fun buildReceiptTemplate(
    sale: Sale,
    cartSnapshot: List<CartLine>,
    customer: Customer?,
    settings: Settings,
    received: Double,
    soldAtFormatted: String,
): ReceiptTemplate {
    val grossSubtotal = cartSnapshot.sumOf { it.unitPrice * it.displayQty }
    val subtotalAfterLineDiscounts = cartSnapshot.sumOf { it.lineTotal }
    val itemDiscountTotal = grossSubtotal - subtotalAfterLineDiscounts
    return ReceiptTemplate(
        storeName = settings.store.name,
        storeAddress = settings.store.address,
        storePhone = settings.store.phone,
        storeTaxId = settings.store.taxId,
        billNo = sale.billNo,
        soldAt = soldAtFormatted,
        customerName = customer?.name.orEmpty(),
        items = cartSnapshot.map { line ->
            ReceiptLine(
                name = line.drug.name,
                displayQty = line.displayQty,
                displayUnit = line.displayUnit,
                unitPrice = line.unitPrice,
                lineTotal = line.lineTotal,
            )
        },
        subtotal = grossSubtotal,
        itemDiscountTotal = itemDiscountTotal,
        cartDiscount = sale.discount,
        total = sale.total,
        received = received,
        change = sale.change,
        pharmacistName = settings.pharmacist.name,
        footer = "ขอบคุณที่ใช้บริการ",
    )
}
