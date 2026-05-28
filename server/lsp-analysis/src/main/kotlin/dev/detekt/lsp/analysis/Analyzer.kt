package dev.detekt.lsp.analysis

import dev.detekt.lsp.document.KotlinParser
import org.eclipse.lsp4j.Diagnostic

/**
 * Façade combining parse + diagnostic publication.
 *
 * Hides the PSI layer entirely from callers — the LSP layer only sees a list of
 * [Diagnostic]s, never a `KtFile`. This keeps `kotlin-compiler-embeddable` out of
 * the server-app classpath.
 *
 * Not thread-safe (the underlying parser isn't). Callers must serialize calls
 * or hold one [Analyzer] per worker.
 */
class Analyzer(
    private val parser: KotlinParser = KotlinParser(),
) : AutoCloseable {

    fun analyze(fileName: String, source: String): List<Diagnostic> {
        val ktFile = parser.parse(fileName, source)
        return DiagnosticPublisher.parseErrorsAsDiagnostics(ktFile)
    }

    override fun close() {
        parser.close()
    }
}
