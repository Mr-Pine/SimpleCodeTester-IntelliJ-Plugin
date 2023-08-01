plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "de.mr-pine"

version = "1.4.1"

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
    version.set("2023.2")
}

dependencies {
    val ktorVersion = "2.3.2"
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-java-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}

configurations.all {
    exclude("org.slf4j", "slf4j-api")
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("232")
        untilBuild.set("241.*")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    register<PrintVersionTask>("printVersion")
    register<CheckVersionTask>("checkVersion")
}

tasks {
    run {
        // workaround for https://youtrack.jetbrains.com/issue/IDEA-285839/Classpath-clash-when-using-coroutines-in-an-unbundled-IntelliJ-plugin
        buildPlugin {
            exclude { "coroutines" in it.name }
        }
        prepareSandbox {
            exclude { "coroutines" in it.name }
        }
    }
}

kotlin {
    jvmToolchain(17)
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
            val compared = newVersion.split(".").mapIndexed { index, s ->
                s.toInt().compareTo(oldVersion.toString().split(".")[index].toInt())
            }
            val major = compared[0]
            val minor = compared[1]
            val patch = compared[2]
            if(major == 1 || (major == 0 && minor == 1) || (major == 0 && minor == 0 && patch == 1)) {
                println("OK. $newVersion > $oldVersion")
            } else {
                throw GradleException("$newVersion must be newer version than $oldVersion")
            }
        } else {
            throw IllegalArgumentException("No new version supplied")
        }
    }
}