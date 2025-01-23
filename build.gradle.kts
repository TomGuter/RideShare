// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath("com.google.gms:google-services:4.3.15")  // הגרסה האחרונה של Google Services Plugin
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
}

// יש להוסיף את השורה הזו כדי להחיל את ה-plugin
apply(plugin = "com.google.gms.google-services")
