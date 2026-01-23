import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Plugin para el manejo de JSON de la API
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    // Registro correcto del target de Android
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // Configuración de targets para iOS
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        // --- DEPENDENCIAS COMUNES (Android e iOS) ---
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Iconos y Lifecycle
            implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            // Ktor para la API de parques
            implementation("io.ktor:ktor-client-core:2.3.12")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

            implementation("media.kamel:kamel-image:0.9.0")

            implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

            implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.40")
        }

        // --- DEPENDENCIAS EXCLUSIVAS DE ANDROID ---
        androidMain.dependencies {
            implementation("com.google.android.material:material:1.12.0")
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Motor de red para Android
            implementation("io.ktor:ktor-client-okhttp:2.3.12")

            // Google Auth (Login)
            implementation("com.google.android.gms:play-services-auth:21.2.0")

            implementation("com.facebook.android:facebook-login:17.0.0")

        }

        // --- DEPENDENCIAS EXCLUSIVAS DE iOS ---
        iosMain.dependencies {
            // Motor de red para iOS (Darwin)
            implementation("io.ktor:ktor-client-darwin:2.3.12")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Herramientas de depuración para Compose en Android
    debugImplementation(compose.uiTooling)
}