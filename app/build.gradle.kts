plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("kotlin-kapt")
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.shareride"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shareride"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "OPENCAGE_API_KEY", "\"${project.findProperty("OPENCAGE_API_KEY")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation(libs.room.runtime)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.dataconnect)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.adapters)
    implementation(libs.androidx.recyclerview)
    implementation(libs.play.services.location)
    kapt(libs.androidx.room.compiler)
    implementation(libs.cloudinary.android)

    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.play.services.location)

    implementation(libs.osmdroid.android)
    implementation(libs.osmdroid.wms)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    implementation (libs.picasso)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)

}



