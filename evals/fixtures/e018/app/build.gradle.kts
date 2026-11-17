plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "<app namespace>"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34
        versionCode = 118
        versionName = "5.4.0"
    }
}

dependencies {
    implementation("dev.sample:chartkit:2.3.1")
    implementation("dev.sample:paylib:3.8.0")
    implementation("androidx.core:core-ktx:1.12.0")
}
