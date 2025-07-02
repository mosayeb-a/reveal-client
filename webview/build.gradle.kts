@file:Suppress("UNUSED_VARIABLE", "OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatorm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
//    explicitApi = ExplicitApiMode.Strict
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "webview"
            isStatic = true
        }
        iosTarget.setUpiOSObserver()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer())
                        .apply {
                            static =
                                (static ?: mutableListOf())
                                    .apply {
                                        add(rootDirPath)
                                        add(projectDirPath)
                                    }
                        }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(libs.kermit)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.kotlin.serialization.json)
        }

        androidMain.dependencies {
            api(libs.android.activity.compose)
            api(libs.android.webkit)
            implementation(libs.kotlin.coroutines.android)
        }

        iosMain.dependencies { }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.common)
            api(libs.kcef)
            implementation(libs.kotlin.coroutines.swing)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.multiplatform.webview"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

fun org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.setUpiOSObserver() {
    val path = projectDir.resolve("src/nativeInterop/cinterop/observer")

    binaries.all {
        linkerOpts("-F $path")
        linkerOpts("-ObjC")
    }

    compilations.getByName("main") {
        cinterops.create("observer") {
            compilerOpts("-F $path")
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}
