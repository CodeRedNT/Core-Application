package br.com.coderednt.coreapp.core.lint

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass

/**
 * Detector que verifica se os ViewModels herdam de [BaseViewModel].
 * 
 * Isso garante a padronização do Unidirectional Data Flow (UDF) em todo o ecossistema.
 */
class BaseViewModelDetector : Detector(), SourceCodeScanner {

    override fun applicableSuperClasses(): List<String>? {
        return listOf("androidx.lifecycle.ViewModel")
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        val evaluator = context.evaluator
        
        // Ignora a própria BaseViewModel
        if (declaration.qualifiedName == "br.com.coderednt.coreapp.core.architecture.BaseViewModel") {
            return
        }

        // Verifica se a classe herda de BaseViewModel
        if (!evaluator.inheritsFrom(declaration, "br.com.coderednt.coreapp.core.architecture.BaseViewModel", false)) {
            context.report(
                ISSUE,
                declaration,
                context.getNameLocation(declaration),
                "Este ViewModel deve herdar de BaseViewModel para garantir a padronização de estado (UDF) do SDK."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MissingBaseViewModelInheritance",
            briefDescription = "Herança de BaseViewModel obrigatória",
            explanation = "Todos os ViewModels do projeto devem estender BaseViewModel para manter a consistência do gerenciamento de estado via UDF.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                BaseViewModelDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
