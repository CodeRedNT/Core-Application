package br.com.coderednt.coreapp.core.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ModuleDependencyTest {

    @Test
    fun `feature modules should not depend on other feature modules`() {
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
                val path = file.path
                val isFeatureFile = path.contains("features/")
                if (isFeatureFile) {
                    file.imports.none { 
                        it.name.contains("br.com.coderednt.coreapp.features") && 
                        !it.name.contains(file.packagee?.name ?: "") 
                    }
                } else {
                    true
                }
            }
    }

    @Test
    fun `core modules should not depend on feature modules`() {
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
                val path = file.path
                val isCoreFile = path.contains("core/")
                if (isCoreFile) {
                    file.imports.none { it.name.contains("br.com.coderednt.coreapp.features") }
                } else {
                    true
                }
            }
    }
}
