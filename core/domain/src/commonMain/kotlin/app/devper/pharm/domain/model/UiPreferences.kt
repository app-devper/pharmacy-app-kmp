package app.devper.pharm.domain.model

enum class ThemePreference(val wire: String) {
    Light("light"),
    Dark("dark"),
    Auto("auto");

    companion object {
        fun parse(raw: String?): ThemePreference = when (raw?.lowercase()) {
            "light" -> Light
            "dark"  -> Dark
            "auto"  -> Auto
            else    -> Auto
        }
    }
}

enum class FontSizePreference(val wire: String, val scale: Float) {
    Sm("sm", 0.875f),
    Md("md", 1.0f),
    Lg("lg", 1.125f),
    Xl("xl", 1.25f);

    companion object {
        fun parse(raw: String?): FontSizePreference = when (raw?.lowercase()) {
            "sm"      -> Sm
            "md"      -> Md
            "lg"      -> Lg
            "xl"      -> Xl
            else      -> Md
        }
    }
}

data class UiPreferences(
    val theme: ThemePreference = ThemePreference.Auto,
    val fontSize: FontSizePreference = FontSizePreference.Md,
) {
    companion object {
        val Default: UiPreferences = UiPreferences()
    }
}
