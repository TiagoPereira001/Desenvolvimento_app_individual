// Ficheiro: Combustivel/settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Adicionar esta linha para encontrar o MPAndroidChart
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "combustivel"
include(":app")