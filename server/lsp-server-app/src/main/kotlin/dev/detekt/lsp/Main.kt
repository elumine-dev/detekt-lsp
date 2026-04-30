package dev.detekt.lsp

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import java.util.concurrent.Executors

/**
 * Entry point. Wires LSP4J JSON-RPC over stdio to [DetektLanguageServer].
 *
 * The VSCode client launches this binary (native or JVM) and communicates via stdin/stdout.
 * Logs MUST go to stderr or a configured file — anything on stdout corrupts the JSON-RPC stream.
 */
fun main() {
    // 4 threads is enough for M0; tuned per workload in later milestones.
    val executor = Executors.newFixedThreadPool(4) { r ->
        Thread(r, "detekt-lsp-rpc").apply { isDaemon = true }
    }

    val server = DetektLanguageServer()
    val launcher = Launcher.Builder<LanguageClient>()
        .setLocalService(server)
        .setRemoteInterface(LanguageClient::class.java)
        .setInput(System.`in`)
        .setOutput(System.out)
        .setExecutorService(executor)
        .create()

    server.connect(launcher.remoteProxy)

    val listening = launcher.startListening()
    listening.get()
    executor.shutdown()
}
