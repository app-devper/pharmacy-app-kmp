package app.devper.pharm.common

sealed class AppException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class AuthException(
    message: String = "กรุณาเข้าสู่ระบบใหม่",
    cause: Throwable? = null,
) : AppException(message, cause)

class ForbiddenException(
    message: String = "ไม่มีสิทธิ์ทำรายการนี้",
    cause: Throwable? = null,
) : AppException(message, cause)

class NotFoundException(
    message: String = "ไม่พบข้อมูล",
    cause: Throwable? = null,
) : AppException(message, cause)

class ConflictException(
    message: String = "เกิดข้อขัดแย้ง — ตรวจสอบข้อมูลและลองใหม่",
    val payload: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause)

class NetworkException(
    message: String = "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์",
    cause: Throwable? = null,
) : AppException(message, cause)

class ServerException(
    message: String = "เซิร์ฟเวอร์ขัดข้อง",
    val statusCode: Int? = null,
    val body: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause)

class ValidationException(
    message: String = "ตรวจสอบข้อมูลไม่ผ่าน",
    cause: Throwable? = null,
) : AppException(message, cause)

class StorageException(
    message: String = "ไม่สามารถบันทึกไฟล์ได้",
    cause: Throwable? = null,
) : AppException(message, cause)

class UnsupportedPlatformException(
    message: String = "ยังไม่รองรับฟีเจอร์นี้บนแพลตฟอร์มนี้",
    cause: Throwable? = null,
) : AppException(message, cause)
