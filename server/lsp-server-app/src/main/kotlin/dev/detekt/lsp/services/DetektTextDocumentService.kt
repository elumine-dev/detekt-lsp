package dev.detekt.lsp.services

import dev.detekt.lsp.analysis.Analyzer
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.TextDocumentService
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

/**
 * M0+ text-document service.
 *
 * Holds an in-memory buffer per open document (full sync — replaced wholesale on
 * every `didChange`), reparses with [KotlinParser] on every edit, and publishes
 * parse-error diagnostics back to the client.
 *
 * M1 will replace the full-buffer model with a per-file PSI cache + incremental
 * `didChange` application.
 */
class DetektTextDocumentService : TextDocumentService {

    private var client: LanguageClient? = null
    private val analyzer = Analyzer()
    private val documents = ConcurrentHashMap<String, String>()

    fun connect(client: LanguageClient) {
        this.client = client
    }

    fun shutdown() {
        analyzer.close()
        documents.clear()
    }

    override fun didOpen(params: DidOpenTextDocumentParams) {
        val uri = params.textDocument.uri
        val text = params.textDocument.text
        documents[uri] = text
        publish(uri, text)
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        val uri = params.textDocument.uri
        // Full sync: a single content-change event carrying the entire new buffer.
        val newText = params.contentChanges.lastOrNull()?.text ?: return
        documents[uri] = newText
        publish(uri, newText)
    }

    override fun didClose(params: DidCloseTextDocumentParams) {
        val uri = params.textDocument.uri
        documents.remove(uri)
        client?.publishDiagnostics(PublishDiagnosticsParams(uri, emptyList()))
    }

    override fun didSave(params: DidSaveTextDocumentParams) {
        // No-op: the in-memory buffer is kept current via didChange.
    }

    @Synchronized
    private fun publish(uri: String, text: String) {
        val client = this.client ?: return
        val diagnostics = analyzer.analyze(fileNameOf(uri), text)
        client.publishDiagnostics(PublishDiagnosticsParams(uri, diagnostics))
    }

    private fun fileNameOf(uri: String): String =
        runCatching { Paths.get(URI.create(uri)).fileName.toString() }
            .getOrElse { "untitled.kt" }
}
