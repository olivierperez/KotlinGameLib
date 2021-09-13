plugins {
    kotlin("multiplatform") version "1.5.30"
}

group = "fr.o80.gamelib"
version = "1.0-SNAPSHOT"

kotlin {
    /*mingwX64("native") { // on Windows
        val main by compilations.getting {
            dependencies {
                implementation(project(":GameLib"))
            }
        }
        binaries {
            executable()
        }
    }*/
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native") {
            val main by compilations.getting {
                dependencies {
                    implementation(project(":GameLib"))
                }
            }
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
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("okio.ExperimentalFileSystem")
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}
