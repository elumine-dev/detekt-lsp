plugins {
    id("detekt-lsp.kotlin-conventions")
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(project(":lsp-document"))
    jmh(project(":lsp-detekt-bridge"))
    jmh(libs.kotlin.stdlib)
}

jmh {
    warmupIterations.set(2)
    iterations.set(5)
    fork.set(2)
    timeUnit.set("ms")
}
