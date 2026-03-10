package br.com.coderednt.coreapp.core.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class BaseActivityDetectorTest {

    @Test
    fun `activity not inheriting from BaseActivity should report error`() {
        lint()
            .allowMissingSdk() // Permite rodar testes sem configurar o ANDROID_HOME explicitamente
            .files(
                kotlin("""
                    package test.pkg
                    import androidx.activity.ComponentActivity
                    class MyActivity : ComponentActivity()
                """).indented(),
                // Mock da BaseActivity para o contexto do lint
                kotlin("""
                    package br.com.coderednt.coreapp.core.architecture
                    import androidx.activity.ComponentActivity
                    abstract class BaseActivity : ComponentActivity()
                """).indented()
            )
            .issues(BaseActivityDetector.ISSUE)
            .run()
            .expect("""
                src/test/pkg/MyActivity.kt:3: Error: Esta Activity deve herdar de BaseActivity para garantir o monitoramento de performance do SDK. [MissingBaseActivityInheritance]
                class MyActivity : ComponentActivity()
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent())
    }

    @Test
    fun `activity inheriting from BaseActivity should not report error`() {
        lint()
            .allowMissingSdk() // Permite rodar testes sem configurar o ANDROID_HOME explicitamente
            .files(
                kotlin("""
                    package br.com.coderednt.coreapp.core.architecture
                    import androidx.activity.ComponentActivity
                    abstract class BaseActivity : ComponentActivity()
                """).indented(),
                kotlin("""
                    package test.pkg
                    import br.com.coderednt.coreapp.core.architecture.BaseActivity
                    class MyActivity : BaseActivity()
                """).indented()
            )
            .issues(BaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }
}
