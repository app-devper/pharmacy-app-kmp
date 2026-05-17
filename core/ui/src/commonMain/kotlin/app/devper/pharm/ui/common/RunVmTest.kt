package app.devper.pharm.ui.common

import app.devper.pharm.common.AppDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun runVmTest(block: suspend TestScope.(AppDispatchers) -> Unit) = runTest {
    val dispatcher = StandardTestDispatcher(testScheduler)
    val testDispatchers = AppDispatchers(main = dispatcher, io = dispatcher, default = dispatcher)
    Dispatchers.setMain(dispatcher)
    try {
        block(testDispatchers)
    } finally {
        Dispatchers.resetMain()
    }
}
