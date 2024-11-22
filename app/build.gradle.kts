plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("kotlin-kapt") // Necesario para Glide
}

android {
    namespace = "com.example.digimontcg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.digimontcg"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12" // Ajusta esta versión según sea necesario
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Firebase BOM: Alinea versiones de Firebase automáticamente
    implementation (platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase servicios específicos
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-database")

    // Glide para carga de imágenes
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    kapt ("com.github.bumptech.glide:compiler:4.15.1")

    // Jetpack Compose y dependencias adicionales
    implementation (platform(libs.androidx.compose.bom))
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.material3:material3")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    implementation (libs.androidx.activity.compose)

            // Dependencias adicionales de Android
            implementation (libs.androidx.core.ktx)
            implementation (libs.androidx.lifecycle.runtime.ktx)
            implementation (libs.androidx.appcompat)
            implementation (libs.material)
            implementation (libs.androidx.activity)
            implementation (libs.androidx.constraintlayout)

            // Dependencias de prueba
            testImplementation (libs.junit)
            androidTestImplementation (libs.androidx.junit)
            androidTestImplementation (libs.androidx.espresso.core)
            debugImplementation ("androidx.compose.ui:ui-tooling")
    debugImplementation ("androidx.compose.ui:ui-test-manifest")
}

// Aplica Google Services al final
apply(plugin = "com.google.gms.google-services")
