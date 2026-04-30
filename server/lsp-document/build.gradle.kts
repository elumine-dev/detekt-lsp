plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(project(":lsp-protocol"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}
