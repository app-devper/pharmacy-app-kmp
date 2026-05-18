package app.devper.pharm.platform

import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.common.platform.FilePicker

class FilePickerImpl : FilePicker {
    override suspend fun pickJsonFile(): Result<String?> = Result.failure(
        UnsupportedPlatformException(
            "การเลือกไฟล์ JSON บน Android ต้องผ่าน Activity result API — ยังไม่รองรับในรุ่นนี้",
        ),
    )
}
