// Aggregator root. Cross-cutting config lives in build-logic/.

tasks.register("listModules") {
    description = "Lists all Gradle subprojects"
    group = "help"
    doLast { subprojects.forEach { println("- ${it.path}") } }
}
