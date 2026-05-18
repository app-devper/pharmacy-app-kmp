package app.devper.pharm.platform

import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.common.platform.FilePicker

class FilePickerImpl : FilePicker {
    override suspend fun pickJsonFile(): Result<String?> = Result.failure(
        UnsupportedPlatformException(
            "การเลือกไฟล์ JSON บน iOS ต้องผ่าน UIDocumentPickerViewController — ยังไม่รองรับในรุ่นนี้",
        ),
    )
}
