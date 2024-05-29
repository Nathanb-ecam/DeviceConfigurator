pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven{url =uri("https://repo.eclipse.org/content/repositories/paho-releases/") }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }

    }
}




rootProject.name = "ArduinoBluetooth"
include(":app")
 