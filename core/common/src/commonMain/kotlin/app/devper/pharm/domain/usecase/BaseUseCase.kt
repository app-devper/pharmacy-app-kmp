package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in P, R>(
    private val dispatchers: AppDispatchers,
) {
    suspend operator fun invoke(param: P): Result<R> =
        withContext(dispatchers.io) {
            runCatching { execute(param) }
        }

    protected abstract suspend fun execute(param: P): R
}

abstract class BaseSyncUseCase<in P, R> {
    operator fun invoke(param: P): Result<R> = runCatching { execute(param) }
    protected abstract fun execute(param: P): R
}
