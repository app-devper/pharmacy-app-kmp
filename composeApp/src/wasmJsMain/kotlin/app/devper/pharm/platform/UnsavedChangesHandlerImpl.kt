package app.devper.pharm.platform

import app.devper.pharm.common.platform.UnsavedChangesHandler

class UnsavedChangesHandlerImpl : UnsavedChangesHandler {
    override fun setHasUnsavedChanges(value: Boolean) {
        updateBeforeUnloadHandler(value)
    }
}

private fun updateBeforeUnloadHandler(value: Boolean): Unit = js(
    """
    {
        window.onbeforeunload = value
            ? function (event) {
                event.preventDefault();
                event.returnValue = '';
            }
            : null;
    }
    """,
)
