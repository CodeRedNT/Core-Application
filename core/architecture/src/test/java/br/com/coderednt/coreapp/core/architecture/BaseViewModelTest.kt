package br.com.coderednt.coreapp.core.architecture

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    data class TestState(val count: Int = 0)
    sealed class TestEvent { object Increment : TestEvent() }

    class TestViewModel : BaseViewModel<TestState, TestEvent>(TestState()) {
        override fun onEvent(event: TestEvent) {
            when (event) {
                TestEvent.Increment -> updateState { copy(count = count + 1) }
            }
        }
    }

    private lateinit var viewModel: TestViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TestViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        assertEquals(0, viewModel.uiState.value.count)
    }

    @Test
    fun `onEvent should update state correctly`() = runTest {
        viewModel.onEvent(TestEvent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.count)
    }
}
