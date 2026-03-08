package br.com.coderednt.coreapp.features.performance.di

import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

/**
 * Teste de Integração do Grafo Hilt para o módulo de Performance.
 * Garante que todos os Trackers e Monitors estão corretamente vinculados.
 */
@HiltAndroidTest
@Config(application = dagger.hilt.android.testing.HiltTestApplication::class, sdk = [33])
@RunWith(RobolectricTestRunner::class)
class PerformanceHiltTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var appHealthTracker: AppHealthTracker

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `hilt should inject AppHealthTracker correctly`() {
        assertNotNull(appHealthTracker)
    }

    @Test
    fun `hilt should inject PerformanceMonitor correctly`() {
        assertNotNull(performanceMonitor)
    }
}
