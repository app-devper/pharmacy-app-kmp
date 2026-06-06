@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain

import app.devper.pharm.common.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

fun testDispatchers(dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()): AppDispatchers =
    AppDispatchers(main = dispatcher, io = dispatcher, default = dispatcher)
