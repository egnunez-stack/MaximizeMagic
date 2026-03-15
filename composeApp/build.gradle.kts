import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.21"
    alias(libs.plugins.google.services)
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    cocoapods {
        summary = "MaximizeMagic"
        homepage = "https://github.com/JetBrains/kotlin"
        version = "1.0.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
        pod("GoogleSignIn") {
            version = "7.1.0"
            extraOpts += listOf(
                "-compiler-option", "-fmodules",
                // Forzamos a Clang a usar una carpeta de caché dentro de tu proyecto
                // Esto evita el error de "ASTReadError" y "Darwin not found"
                "-compiler-option", "-fmodules-cache-path=${layout.buildDirectory.get()}/clang-module-cache",
                "-compiler-option", "-fno-modules-validate-system-headers",
                "-compiler-option", "-Wno-error=unused-command-line-argument",
                "-compiler-option", "-D__IPHONE_OS_VERSION_MIN_REQUIRED=140100"
            )
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Forzar compatibilidad con Xcode 16 y omitir avisos de versión
    targets.withType<KotlinNativeTarget> {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xexpect-actual-classes",
                "-Xoverride-konan-properties=appleSdkRoot=$(xcrun --sdk iphoneos --show-sdk-path)",
                "-Pkotlin.apple.xcodeCompatibility.nowarn=true"
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)

            implementation(libs.kamel.image)
            implementation(libs.multiplatform.settings)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.webview)
        }

        androidMain.dependencies {
            implementation(libs.google.material)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.play.services.auth)
            implementation(libs.facebook.login)
            implementation(libs.play.services.ads)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.gen.maximizemagic"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gen.maximizemagic"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
