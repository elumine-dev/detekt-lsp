plugins {
    id("detekt-lsp.kotlin-conventions")
    application
    alias(libs.plugins.shadow)
    alias(libs.plugins.graalvm.native)
}

application {
    mainClass.set("dev.detekt.lsp.MainKt")
    applicationDefaultJvmArgs = listOf(
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=50",
        "-Xms256m",
        "-Xmx1g",
    )
}

dependencies {
    implementation(project(":lsp-protocol"))
    implementation(project(":lsp-analysis"))
    // lsp-analysis brings lsp-document + lsp4j transitively (api dependencies).

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    runtimeOnly(libs.slf4j.simple)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions)
}

// Fat-jar fallback for users without GraalVM native binary on their platform.
tasks.shadowJar {
    archiveBaseName.set("detekt-lsp")
    archiveClassifier.set("all")
    mergeServiceFiles()
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("detekt-lsp")
            mainClass.set("dev.detekt.lsp.MainKt")
            buildArgs.addAll(
                "--no-fallback",
                "-O3",
                "--enable-preview",
                "-H:+UnlockExperimentalVMOptions",
            )
        }
    }
}
