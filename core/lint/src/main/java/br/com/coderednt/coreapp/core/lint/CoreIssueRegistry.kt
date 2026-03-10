package br.com.coderednt.coreapp.core.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/**
 * Registro central das regras de lint customizadas do SDK.
 */
class CoreIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(BaseActivityDetector.ISSUE)

    override val api: Int
        get() = CURRENT_API

    override val vendor: Vendor = Vendor(
        vendorName = "CodeRednt Core",
        feedbackUrl = "https://github.com/coderednt/core-app/issues",
        contact = "https://coderednt.com"
    )
}
