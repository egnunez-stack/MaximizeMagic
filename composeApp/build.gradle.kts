import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.google.services)
    kotlin("native.cocoapods")
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Logic-only shared module for MaximizeMagic"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "SharedTodo"
            isStatic = true
        }
        
        pod("GoogleSignIn") {
            version = "7.1.0"
            extraOpts += listOf(
                "-compiler-option", "-fmodules",
                "-compiler-option", "-fmodules-cache-path=${layout.buildDirectory.asFile.get().absolutePath}/clang-module-cache"
            )
        }
        pod("AppAuth") {
            version = "1.7.6"
        }
        pod("GTMAppAuth") {
            version = "4.1.1"
        }
    }

    targets.withType<KotlinNativeTarget> {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xexpect-actual-classes"
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
