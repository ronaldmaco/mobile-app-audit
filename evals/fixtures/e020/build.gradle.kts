android {
    namespace = "app.circles"
    compileSdk = 36
    defaultConfig {
        applicationId = "app.circles"
        minSdk = 26
        targetSdk = 36
        versionCode = 41
        versionName = "3.2.0"
    }
}

dependencies {
    implementation("vendor.sdk:analytics:9.1.0")
    implementation("vendor.sdk:attribution:6.4.2")
    implementation("vendor.sdk:ads:23.0.0")
    implementation("vendor.sdk:consent:2.3.0")
    implementation("androidx.work:work-runtime-ktx:2.10.1")
}
