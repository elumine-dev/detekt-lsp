plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(project(":lsp-protocol"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}
