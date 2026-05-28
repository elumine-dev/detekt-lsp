package dev.detekt.lsp.protocol

/**
 * Static metadata exposed to LSP clients via the `initialize` response.
 * Versions are wired in at release time via gradle resource filtering (added M9).
 */
object ServerInfo {
    const val NAME: String = "detekt-lsp"
    const val VERSION: String = "0.1.0"
    const val LSP_SPEC_VERSION: String = "3.18"
}
