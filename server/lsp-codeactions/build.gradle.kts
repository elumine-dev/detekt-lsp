plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(project(":lsp-detekt-bridge"))
    implementation(libs.kotlin.stdlib)

    testImplementation(libs.junit.jupiter)
}
