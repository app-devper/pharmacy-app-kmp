package app.devper.pharm.ui.format

fun formatBaht(value: Double): String {
    val cents = (value * 100.0 + if (value >= 0) 0.5 else -0.5).toLong()
    val negative = cents < 0
    val absCents = if (negative) -cents else cents
    val whole = absCents / 100
    val frac = (absCents % 100).toString().padStart(2, '0')
    val grouped = whole.toString().reversed().chunked(3).joinToString(",").reversed()
    return if (negative) "-$grouped.$frac" else "$grouped.$frac"
}

fun formatBahtCurrency(value: Double): String = "฿${formatBaht(value)}"
