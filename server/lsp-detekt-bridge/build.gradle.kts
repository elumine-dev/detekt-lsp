plugins {
    id("detekt-lsp.kotlin-conventions")
}

dependencies {
    api(project(":lsp-analysis"))
    implementation(libs.detekt.api)
    implementation(libs.detekt.core)
    runtimeOnly(libs.detekt.rules)
    runtimeOnly(libs.detekt.formatting)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.detekt.test)
    testImplementation(libs.kotest.assertions)
}
