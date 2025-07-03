import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatorm)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.android.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)

            api(project(":webview"))

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.ui.backhandler)

            implementation(libs.material.icons.core)
            implementation(libs.filekit.core)

            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)

            implementation(libs.napier)
            implementation(libs.material3.adaptive)

            api(libs.datastore.preferences)
            api(libs.datastore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(libs.kotlin.coroutines.swing)
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "app.ma.reveal"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.ma.reveal"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "app.ma.reveal.MainKt"

        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "app.ma.reveal"
            packageVersion = "1.0.0"
        }
    }
}
tasks.register("runDesktop") {
    group = "application"
    description = "Runs the Compose Desktop application"
    dependsOn(":composeApp:run")
}

tasks.register<Exec>("runDesktopHotReload") {
    group = "application"
    description = "Runs the Compose Desktop app with hot reload"
    workingDir = rootDir
    commandLine = listOf(
        "./gradlew",
        ":composeApp:desktopRunHot",
        "--mainClass", "app.ma.reveal.MainKt",
        "--autoReload"
    )
}

