plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(project(":lsp-document"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)

    testImplementation(libs.junit.jupiter)
}
