package dev.detekt.lsp.analysis

import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile

/**
 * Walks the PSI tree of a [KtFile] and produces one LSP [Diagnostic] per
 * `PsiErrorElement` (unparseable region).
 *
 * Tier 0 diagnostics — purely syntactic, no rule engine wired in yet.
 * M2+ will layer detekt Tier 1 (syntactic rules) on top of these.
 */
object DiagnosticPublisher {

    private const val SOURCE = "detekt-lsp"
    private const val FALLBACK_MESSAGE = "syntax error"

    fun parseErrorsAsDiagnostics(ktFile: KtFile): List<Diagnostic> {
        val text = ktFile.text
        return PsiTreeUtil.findChildrenOfType(ktFile, PsiErrorElement::class.java)
            .map { it.toDiagnostic(text) }
    }

    private fun PsiErrorElement.toDiagnostic(source: String): Diagnostic {
        val start = textRange.startOffset
        // Zero-width error ranges hide the wavy line in some editors — extend by 1.
        val end = textRange.endOffset.coerceAtLeast(start + 1)
        val message = errorDescription.ifBlank { FALLBACK_MESSAGE }
        return Diagnostic(
            Range(source.offsetToPosition(start), source.offsetToPosition(end)),
            message,
            DiagnosticSeverity.Error,
            SOURCE,
        )
    }
}

/**
 * Convert a character offset in [this] string to an LSP [Position]
 * (zero-based line, zero-based UTF-16 column).
 *
 * LSP positions are end-of-line exclusive: an offset that lands on `\n` belongs
 * to the line that contains the newline character.
 */
internal fun String.offsetToPosition(offset: Int): Position {
    val safe = offset.coerceIn(0, length)
    var line = 0
    var lastNewline = -1
    for (i in 0 until safe) {
        if (this[i] == '\n') {
            line++
            lastNewline = i
        }
    }
    return Position(line, safe - lastNewline - 1)
}
