package app.devper.pharm.ui.i18n.groups

object CalendarStringsTh : CalendarStrings {
    override val calendarMonthJanuary = "มกราคม"
    override val calendarMonthFebruary = "กุมภาพันธ์"
    override val calendarMonthMarch = "มีนาคม"
    override val calendarMonthApril = "เมษายน"
    override val calendarMonthMay = "พฤษภาคม"
    override val calendarMonthJune = "มิถุนายน"
    override val calendarMonthJuly = "กรกฎาคม"
    override val calendarMonthAugust = "สิงหาคม"
    override val calendarMonthSeptember = "กันยายน"
    override val calendarMonthOctober = "ตุลาคม"
    override val calendarMonthNovember = "พฤศจิกายน"
    override val calendarMonthDecember = "ธันวาคม"
    override val calendarWeekdaySun = "อา"
    override val calendarWeekdayMon = "จ"
    override val calendarWeekdayTue = "อ"
    override val calendarWeekdayWed = "พ"
    override val calendarWeekdayThu = "พฤ"
    override val calendarWeekdayFri = "ศ"
    override val calendarWeekdaySat = "ส"
    override val calendarYear: (Int) -> String = { y -> "${y + 543}" }
    override val calendarToday = "วันนี้"
    override val calendarPrevMonth = "เดือนก่อนหน้า"
    override val calendarNextMonth = "เดือนถัดไป"
}
