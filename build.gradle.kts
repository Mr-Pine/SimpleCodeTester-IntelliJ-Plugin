plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.11.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
}

group = "de.mr-pine"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.3.1")
}

dependencies {
    val ktorVersion = "2.2.1"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("223")
        untilBuild.set("231.*")
    }

    withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    register<PrintVersionTask>("printVersion")
    register<CheckVersionTask>("checkVersion")
}

abstract class PrintVersionTask : DefaultTask() {
    @TaskAction
    fun printVersion() {
        println(project.version)
    }
}

abstract class CheckVersionTask @Inject constructor() : DefaultTask() {

    @TaskAction
    fun checkVersion() {
        val newVersion = project.properties["newVersion"]?.toString()
        val oldVersion = project.version

        if (newVersion != null) {
            newVersion.split(".").forEachIndexed { index, s ->
                if (oldVersion.toString().split(".")[index] != s) throw Exception("Must be newer version")
            }
        } else {
            throw IllegalArgumentException("No new version supplied")
        }
    }
}
