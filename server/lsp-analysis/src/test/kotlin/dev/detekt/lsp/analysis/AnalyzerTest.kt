package dev.detekt.lsp.analysis

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.eclipse.lsp4j.DiagnosticSeverity
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnalyzerTest {

    private val analyzer = Analyzer()

    @AfterAll
    fun closeAnalyzer() {
        analyzer.close()
    }

    @Test
    fun `valid kotlin produces zero diagnostics`() {
        analyzer.analyze("Foo.kt", "fun foo() = 42\n").shouldBeEmpty()
    }

    @Test
    fun `broken kotlin yields at least one error-severity diagnostic`() {
        val diagnostics = analyzer.analyze("Broken.kt", "fun foo() {\n")
        diagnostics.isNotEmpty() shouldBe true
        diagnostics.forEach { it.severity shouldBe DiagnosticSeverity.Error }
    }

    @Test
    fun `every diagnostic is sourced as detekt-lsp`() {
        val diagnostics = analyzer.analyze("Broken.kt", "class Foo {\n")
        diagnostics.forEach { it.source shouldBe "detekt-lsp" }
    }
}
