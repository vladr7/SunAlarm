@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.riviem.sunalarm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.riviem.sunalarm"
        minSdk = 27
        targetSdk = 34
        versionCode = 8
        versionName = "0.9.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.bundles.compose)
    implementation(libs.gms.auth)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.androidx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil)
    implementation(libs.media3)
    implementation(libs.media3Ui)
    implementation(libs.media3Session)
    implementation(libs.kotlinx.serialization)
    implementation(libs.datastore)
    implementation(libs.bundles.retrofitAndSerialization)
    implementation(libs.squareup.okHttp)
    implementation(libs.maps.playServices)
    implementation(libs.maps.compose)
    implementation(libs.playServiceCodeScanner)
    implementation(libs.play.services.location)
    kapt(libs.google.hiltandroidcompiler)
    implementation(libs.lottie)
    implementation(libs.image.compressor)
    implementation(libs.play.review)
    implementation(libs.play.review.ktx)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.appupdate)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.play.services.basement)
    implementation(libs.colorpicker.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.coroutines)
    annotationProcessor(libs.room.annotationProcessor)
    kapt(libs.room.annotationProcessor)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.compose.testing)
    testImplementation(libs.bundles.testing)
    debugImplementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.navigation)
    testCompileOnly(libs.hamcrest)
    testCompileOnly(libs.kotlinx.coroutines.test)
}