// build.gradle.kts (Project: Combustivel)

plugins {
    // Declara o plugin de "Aplicação Android"
    // 'apply false' diz: "Este plugin existe, mas sera aplicado nos modulos (na pasta 'app'), nao aqui."
    id("com.android.application") version "8.2.0" apply false
}

// Nao e preciso mais nada aqui.
// Nao precisamos de 'kapt' nem 'kotlin' aqui, porque o projeto e Java.