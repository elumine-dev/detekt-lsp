package dev.detekt.lsp.document

import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile

/**
 * Parses Kotlin source into a [KtFile] PSI tree.
 *
 * Backed by a single shared [KotlinCoreEnvironment] — the environment holds the
 * application-level PSI infrastructure (project, file factories) and is expensive
 * to construct. Each [parse] call is cheap.
 *
 * Not thread-safe: callers must serialize parses, or hold one parser per worker thread.
 * Call [close] on server shutdown to release the underlying Disposable.
 */
class KotlinParser : AutoCloseable {

    private val disposable = Disposer.newDisposable("detekt-lsp KotlinParser")

    private val environment: KotlinCoreEnvironment = run {
        val config = CompilerConfiguration().apply {
            put(
                CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
                PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false),
            )
        }
        KotlinCoreEnvironment.createForProduction(
            disposable,
            config,
            EnvironmentConfigFiles.JVM_CONFIG_FILES,
        )
    }

    private val fileFactory: PsiFileFactory = PsiFileFactory.getInstance(environment.project)

    /**
     * Parse [source] as a Kotlin file.
     *
     * [virtualFileName] is cosmetic — used by the PSI layer when reporting positions
     * and in error messages. Pass the basename of the LSP document URI (e.g., "Foo.kt").
     *
     * The returned [KtFile] contains the full syntactic PSI tree, including any
     * `PsiErrorElement` nodes for unparseable regions. Semantic resolution
     * (types, references) is NOT performed at this layer.
     */
    fun parse(virtualFileName: String, source: String): KtFile {
        val psiFile = fileFactory.createFileFromText(
            virtualFileName,
            KotlinFileType.INSTANCE,
            source,
        )
        return psiFile as KtFile
    }

    override fun close() {
        Disposer.dispose(disposable)
    }
}
