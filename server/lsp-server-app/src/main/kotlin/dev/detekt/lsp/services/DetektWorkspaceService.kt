package dev.detekt.lsp.services

import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.WorkspaceService

/**
 * M0 stub: logs workspace events. Config loading and file watching at M2 / M6.
 */
class DetektWorkspaceService : WorkspaceService {

    private var client: LanguageClient? = null

    fun connect(client: LanguageClient) {
        this.client = client
    }

    override fun didChangeConfiguration(params: DidChangeConfigurationParams) {
        log("didChangeConfiguration")
    }

    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams) {
        log("didChangeWatchedFiles ${params.changes.size} change(s)")
    }

    private fun log(msg: String) {
        client?.logMessage(MessageParams(MessageType.Log, msg))
    }
}
