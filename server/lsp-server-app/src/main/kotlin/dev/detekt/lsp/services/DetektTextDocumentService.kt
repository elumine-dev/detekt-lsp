package dev.detekt.lsp.services

import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.TextDocumentService

/**
 * M0 stub: logs document lifecycle events. Real PSI cache wired at M1.
 */
class DetektTextDocumentService : TextDocumentService {

    private var client: LanguageClient? = null

    fun connect(client: LanguageClient) {
        this.client = client
    }

    override fun didOpen(params: DidOpenTextDocumentParams) {
        log("didOpen ${params.textDocument.uri}")
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        log("didChange ${params.textDocument.uri} v${params.textDocument.version}")
    }

    override fun didClose(params: DidCloseTextDocumentParams) {
        log("didClose ${params.textDocument.uri}")
    }

    override fun didSave(params: DidSaveTextDocumentParams) {
        log("didSave ${params.textDocument.uri}")
    }

    private fun log(msg: String) {
        client?.logMessage(MessageParams(MessageType.Log, msg))
    }
}
