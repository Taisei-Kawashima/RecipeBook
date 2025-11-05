// kotlin
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    alias(libs.plugins.kotlin.serialization)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

// local.properties を読み込む
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

// Supabase の匿名キーを local.properties から取得
val supabaseAnonKey = localProps.getProperty("SUPABASE_ANON_KEY").orEmpty().trim()
val supabaseAnonKeyDebug = localProps.getProperty("SUPABASE_ANON_KEY_DEBUG").orEmpty().trim().ifEmpty { supabaseAnonKey }
val supabaseAnonKeyRelease = localProps.getProperty("SUPABASE_ANON_KEY_RELEASE").orEmpty().trim().ifEmpty { supabaseAnonKey }

// GEMINI API キーを local.properties から取得
val geminiApiKey = localProps.getProperty("GEMINI_API_KEY").orEmpty().trim()
val geminiApiKeyDebug = localProps.getProperty("GEMINI_API_KEY_DEBUG").orEmpty().trim().ifEmpty { geminiApiKey }
val geminiApiKeyRelease = localProps.getProperty("GEMINI_API_KEY_RELEASE").orEmpty().trim().ifEmpty { geminiApiKey }

android {
    namespace = "namake.recipebook"
    compileSdk = 36

    defaultConfig {
        applicationId = "namake.recipebook"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // local.properties の値を BuildConfig に埋め込む
        // Supabase 匿名キー
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        // GEMINI API キーを BuildConfig に埋め込む
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            // デバッグ用 Supabase 匿名キー
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKeyDebug\"")
            // デバッグ用 GEMINI キー
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKeyDebug\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // リリース用 Supabase 匿名キー
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKeyRelease\"")
            // リリース用 GEMINI キー
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKeyRelease\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// secrets-gradle-plugin の簡易設定（必要に応じて使用）
secrets {
    // プラグインがサポートしている場合、読み込みファイル名を指定
    defaultPropertiesFileName = "local.properties"
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx) // For Photo Picker

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Image Loading
    implementation(libs.coil.compose)

    // Supabase と Serialization
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.kotlinx.serialization.json)

    // Ktor クライアント
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)

    // Gemini用のAPI
    implementation(libs.google.generativeai)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}