plugins {
    // Alterado de 'alias(libs.plugins.android.application)' para evitar conflito de versão.
    // O plugin já está no classpath (versão 8.2.0) carregado pelo projeto raiz.
    id("com.android.application")
}

android {
    // ALTERADO: Novo namespace único.
    namespace = "com.tiagopereira.combustivel"
    // Atualizado para 35 conforme requisitos da Google Play
    compileSdk = 35

    defaultConfig {
        // ALTERADO: Novo applicationId único para a Play Store.
        // Se já tiveres um domínio (ex: tiagopereira.com), usa com.tiagopereira.bombaeficha
        applicationId = "com.tiagopereira.combustivel"
        minSdk = 24
        // Atualizado para 35 conforme requisitos da Google Play
        targetSdk = 35
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // ATIVADO: Reduz o tamanho do código e obfusca nomes de classes/métodos
            isMinifyEnabled = true
            // ATIVADO: Remove recursos (imagens/layouts) que não estão a ser usados
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room components
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // Google Mobile Ads (AdMob) - Necessário para corrigir o erro 'adSize not found'
    implementation("com.google.android.gms:play-services-ads:23.0.0")

    // MPAndroidChart - Necessário para corrigir o erro 'package com.github.mikephil.charting.charts does not exist'
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Google Play Billing Library - Necessário para corrigir o erro 'package com.android.billingclient.api does not exist'
    implementation("com.android.billingclient:billing:6.1.0")
}