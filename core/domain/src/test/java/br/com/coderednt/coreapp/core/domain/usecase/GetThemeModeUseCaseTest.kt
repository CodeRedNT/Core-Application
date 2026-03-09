package br.com.coderednt.coreapp.core.domain.usecase

import br.com.coderednt.coreapp.core.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetThemeModeUseCaseTest {

    private val repository: SettingsRepository = mockk()
    private lateinit var useCase: GetThemeModeUseCase

    @Before
    fun setup() {
        useCase = GetThemeModeUseCase(repository)
    }

    @Test
    fun `invoke should return theme from repository`() = runTest {
        // Given
        val expectedTheme = "DARK"
        every { repository.getThemeMode() } returns flowOf(expectedTheme)

        // When
        val result = useCase()

        // Then
        result.collect { theme ->
            assertEquals(expectedTheme, theme)
        }
    }
}
