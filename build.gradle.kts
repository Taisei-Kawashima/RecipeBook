// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies{
        // local properties から API キーを安全に読み込むためのプラグイン
        classpath(libs.secrets.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}