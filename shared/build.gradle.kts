plugins {
    kotlin("multiplatform")
    id("co.touchlab.native.cocoapods")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(Versions.min_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }
}

kotlin {
    android()
    // Revert to just ios() when gradle plugin can properly resolve it
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    version = "1.1"

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets["commonMain"].dependencies {
        implementation(kotlin("stdlib-common", Versions.kotlin))
        implementation(Deps.SqlDelight.runtime)
        implementation(Deps.Ktor.commonCore)
        implementation(Deps.Ktor.commonJson)
        implementation(Deps.Ktor.commonLogging)
        implementation(Deps.Coroutines.common)
        implementation(Deps.stately)
        implementation(Deps.multiplatformSettings)
        implementation(Deps.koinCore)
        implementation(Deps.Ktor.commonSerialization)
        api(Deps.kermit)
    }

    sourceSets["commonTest"].dependencies {
        implementation(Deps.multiplatformSettingsTest)
        implementation(Deps.KotlinTest.common)
        implementation(Deps.KotlinTest.annotations)
        implementation(Deps.koinTest)
        // Karmok is an experimental library which helps with mocking interfaces
        implementation(Deps.karmok)
    }

    sourceSets["androidMain"].dependencies {
        implementation(kotlin("stdlib", Versions.kotlin))
        implementation(Deps.SqlDelight.driverAndroid)
        implementation(Deps.Ktor.jvmCore)
        implementation(Deps.Ktor.jvmJson)
        implementation(Deps.Ktor.jvmLogging)
        implementation(Deps.Coroutines.android)
        implementation(Deps.Ktor.androidSerialization)
        implementation(Deps.Ktor.androidCore)
        implementation ("net.zetetic:android-database-sqlcipher:4.4.0")
        implementation ("androidx.sqlite:sqlite:2.0.1")
    }

    sourceSets["androidTest"].dependencies {
        implementation(Deps.KotlinTest.jvm)
        implementation(Deps.KotlinTest.junit)
        implementation(Deps.AndroidXTest.core)
        implementation(Deps.AndroidXTest.junit)
        implementation(Deps.AndroidXTest.runner)
        implementation(Deps.AndroidXTest.rules)
        implementation(Deps.Coroutines.test)
        implementation(Deps.robolectric)
    }

    sourceSets["iosMain"].dependencies {
        implementation(Deps.SqlDelight.driverIos)
        implementation(Deps.Ktor.ios)
        implementation("co.touchlab:sqliter:0.7.1") {
            version {
                strictly("0.7.1")
            }
        }
        implementation(Deps.Coroutines.common) {
            version {
                strictly(Versions.coroutines)
            }
        }
        implementation(Deps.koinCore)
    }

    cocoapodsext {
        summary = "Common library for the KaMP starter kit"
        homepage = "https://github.com/touchlab/KaMPKit"
        pod("SQLCipher", "~> 4.0")
        framework {
            isStatic = true
            export(Deps.kermit)
            transitiveExport = true
        }
    }
}

sqldelight {
    database("KaMPKitDb") {
        packageName = "co.touchlab.kampkit.db"
        linkSqlite = false
    }
}
