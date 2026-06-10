package app.devper.pharm.ui.i18n.groups

object CalendarStringsEn : CalendarStrings {
    override val calendarMonthJanuary = "January"
    override val calendarMonthFebruary = "February"
    override val calendarMonthMarch = "March"
    override val calendarMonthApril = "April"
    override val calendarMonthMay = "May"
    override val calendarMonthJune = "June"
    override val calendarMonthJuly = "July"
    override val calendarMonthAugust = "August"
    override val calendarMonthSeptember = "September"
    override val calendarMonthOctober = "October"
    override val calendarMonthNovember = "November"
    override val calendarMonthDecember = "December"
    override val calendarWeekdaySun = "Su"
    override val calendarWeekdayMon = "Mo"
    override val calendarWeekdayTue = "Tu"
    override val calendarWeekdayWed = "We"
    override val calendarWeekdayThu = "Th"
    override val calendarWeekdayFri = "Fr"
    override val calendarWeekdaySat = "Sa"
    override val calendarYear: (Int) -> String = { y -> "$y" }
    override val calendarToday = "Today"
    override val calendarPrevMonth = "Previous month"
    override val calendarNextMonth = "Next month"
}
