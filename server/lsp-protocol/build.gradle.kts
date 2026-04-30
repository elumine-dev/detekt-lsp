plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(libs.lsp4j)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions)
}
