plugins {
    kotlin("multiplatform") version "1.5.30"
}

group = "fr.o80.gamelib"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native") {
            val main by compilations.getting {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
                }
            }
            val interop by main.cinterops.creating
        }
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}
