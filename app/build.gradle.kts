plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.systemtech.update"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.systemtech.update"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    //lottie by airbnb dependency for animations
    implementation(libs.lottie)
    // room database dependency from jetpack android
    implementation(libs.room.runtime)
    // annotation processor dependency from jetpack android
    annotationProcessor(libs.room.compiler)
    // work manager dependency from android to handle background tasks
    implementation(libs.work.runtime)
    // card view dependency from jetpack android
    implementation(libs.cardview)
    // gson dependency from google to convert any data type to json n back
    implementation (libs.gson)
    // dependencies for instrument testing
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit.v115)

    // ✅ WorkManager Testing — allows you to test background Workers in isolation
    androidTestImplementation(libs.work.testing)

    // ✅ AndroidX Test Core — provides Android-related testing utilities (e.g., ApplicationProvider)
    androidTestImplementation(libs.core)

    // ✅ AndroidX Test Runner — needed to run instrumentation tests on Android devices
    androidTestImplementation(libs.runner)

    // ✅ AndroidX Test Rules — helps control lifecycle and threading for tests
    androidTestImplementation(libs.rules)

    // ✅ Room Testing — allows use of in-memory Room DB during instrumented tests
    androidTestImplementation(libs.room.testing)

    // ✅ (Optional) Mockito — if you need to mock any dependencies in your test
    androidTestImplementation(libs.mockito.core.v4110)

    // Truth assertion library
    androidTestImplementation(libs.truth) // or latest

    // WorkManager test helper for instrumented or local testing
    androidTestImplementation(libs.work.testing.v290) // or your matching version

    // ✅ Add this for Robolectric-based testing on JVM (optional unless using Robolectric)
    testImplementation(libs.robolectric)

    // Core dependencies for application to work
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}