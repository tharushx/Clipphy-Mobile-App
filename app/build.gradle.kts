plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.app.clipphy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.clipphy"
        minSdk = 27
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }


}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.code.gson:gson:2.8.0")
    implementation ("androidx.core:core-ktx:1.3.2")
    implementation ("androidx.biometric:biometric:1.2.0-alpha03")
    implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.17")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("androidx.sqlite:sqlite:2.1.0")

}