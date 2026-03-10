package br.com.coderednt.coreapp.core.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiClass
import org.jetbrains.uast.UClass

/**
 * Detector que verifica se as Activities herdam de [BaseActivity].
 * 
 * Isso garante que o monitoramento automático de performance e outras 
 * capacidades do SDK sejam aplicados corretamente em todas as telas.
 */
class BaseActivityDetector : Detector(), SourceCodeScanner {

    override fun applicableSuperClasses(): List<String>? {
        return listOf("androidx.activity.ComponentActivity", "androidx.fragment.app.FragmentActivity", "android.app.Activity")
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        val evaluator = context.evaluator
        
        // Ignora a própria BaseActivity
        if (declaration.qualifiedName == "br.com.coderednt.coreapp.core.architecture.BaseActivity") {
            return
        }

        // Verifica se a classe herda de BaseActivity
        if (!evaluator.inheritsFrom(declaration, "br.com.coderednt.coreapp.core.architecture.BaseActivity", false)) {
            context.report(
                ISSUE,
                declaration,
                context.getNameLocation(declaration),
                "Esta Activity deve herdar de BaseActivity para garantir o monitoramento de performance do SDK."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MissingBaseActivityInheritance",
            briefDescription = "Herança de BaseActivity obrigatória",
            explanation = "Todas as Activities do projeto devem estender BaseActivity para habilitar o monitoramento automático de performance e telemetria.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                BaseActivityDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
