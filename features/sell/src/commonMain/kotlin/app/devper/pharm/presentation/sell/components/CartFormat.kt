package app.devper.pharm.presentation.sell.components

internal fun bahtAmount(value: Double): String {
    val cents = (value * 100.0 + if (value >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).let { if (it < 0) -it else it }
        .toString().padStart(2, '0')
    val sign = if (cents < 0) "-" else ""
    val absWhole = if (whole < 0) -whole else whole
    return "$sign$absWhole.$frac"
}
