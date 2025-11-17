// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.combustivel"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.combustivel"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Diz ao Room onde guardar o "mapa" da BD (o schema)
        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("room.schemaLocation" to "$projectDir/schemas"))
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Forca o Gradle a usar o JDK 17 (corrige o erro 'jlink.exe')
    kotlinOptions {
        jvmTarget = "17"
    }

    // Nome corrigido para 'packaging'
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // UI - Material Design, Constraint & Recycler
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Base de Dados (Room)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // Usar 'kapt'

    // Anuncios (AdMob)
    implementation("com.google.android.gms:play-services-ads:23.0.0")

    // Pagamentos (Billing)
    implementation("com.android.billingclient:billing:6.2.1")

    // Graficos (Corrige o erro `Could not find com.github.PhilJay:MPAndroidChart`)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}