package app.devper.pharm.common.platform

interface FilePicker {
    suspend fun pickJsonFile(): Result<String?>
}
