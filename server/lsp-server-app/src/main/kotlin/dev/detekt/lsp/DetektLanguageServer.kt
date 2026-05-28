package dev.detekt.lsp

import dev.detekt.lsp.protocol.ServerInfo
import dev.detekt.lsp.services.DetektTextDocumentService
import dev.detekt.lsp.services.DetektWorkspaceService
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.InitializedParams
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.ServerInfo as LspServerInfo
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

class DetektLanguageServer : LanguageServer, LanguageClientAware {

    private lateinit var client: LanguageClient
    private val textDocumentService = DetektTextDocumentService()
    private val workspaceService = DetektWorkspaceService()

    override fun initialize(params: InitializeParams): CompletableFuture<InitializeResult> {
        val capabilities = ServerCapabilities().apply {
            // M0+: Full sync — every change ships the whole buffer.
            // M1 switches to Incremental once the PSI cache + reparse layer lands.
            setTextDocumentSync(TextDocumentSyncKind.Full)
        }
        val info = LspServerInfo(ServerInfo.NAME, ServerInfo.VERSION)
        return CompletableFuture.completedFuture(InitializeResult(capabilities, info))
    }

    override fun initialized(params: InitializedParams) {
        client.logMessage(MessageParams(MessageType.Info, "${ServerInfo.NAME} ${ServerInfo.VERSION} ready"))
    }

    override fun shutdown(): CompletableFuture<Any> {
        textDocumentService.shutdown()
        return CompletableFuture.completedFuture(null)
    }

    override fun exit() {
        // For stdio transport, the launcher returns from startListening on stream close.
    }

    override fun getTextDocumentService() = textDocumentService
    override fun getWorkspaceService() = workspaceService

    override fun connect(client: LanguageClient) {
        this.client = client
        textDocumentService.connect(client)
        workspaceService.connect(client)
    }
}
