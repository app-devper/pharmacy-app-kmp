package app.devper.pharm

@JsModule("@js-joda/timezone")
external object JsJodaTimezone

internal fun loadJsJodaTimezoneDatabase() {
    JsJodaTimezone
}
