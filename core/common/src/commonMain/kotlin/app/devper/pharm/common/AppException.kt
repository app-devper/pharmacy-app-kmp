package app.devper.pharm.common

abstract class AppException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class AuthException(
    message: String = "Authentication required",
    cause: Throwable? = null,
) : AppException(message, cause)

class ForbiddenException(
    message: String = "Forbidden",
    cause: Throwable? = null,
) : AppException(message, cause)

class NotFoundException(
    message: String = "Not found",
    cause: Throwable? = null,
) : AppException(message, cause)

class ConflictException(
    message: String = "Conflict",
    val payload: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause)

class NetworkException(
    message: String = "Network unavailable",
    cause: Throwable? = null,
) : AppException(message, cause)

class ServerException(
    message: String = "Server error",
    val statusCode: Int? = null,
    val body: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause)

class ValidationException(
    message: String = "Validation failed",
    cause: Throwable? = null,
) : AppException(message, cause)

class StorageException(
    message: String = "Storage error",
    cause: Throwable? = null,
) : AppException(message, cause)

class UnsupportedPlatformException(
    message: String = "Unsupported on this platform",
    cause: Throwable? = null,
) : AppException(message, cause)
