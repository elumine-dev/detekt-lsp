plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(project(":lsp-document"))
    api(libs.lsp4j)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions)
}
