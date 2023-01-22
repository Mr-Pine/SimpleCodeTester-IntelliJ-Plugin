plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.11.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
}

group = "de.mr-pine"
version = "1.1.0"

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
    val ktorVersion = "2.2.2"
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-java-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-auth-jvm:$ktorVersion")
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
