package app.devper.pharm.ui.components

import app.devper.pharm.common.AppException

fun <T : AppException> T?.unlessPageShowsError(pageIsEmpty: Boolean): T? =
    if (pageIsEmpty) null else this
