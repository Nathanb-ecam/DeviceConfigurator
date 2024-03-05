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
        //maven { url = uri("https://maven.taktik.be/content/groups/public") }
        google()
        mavenCentral()
        mavenLocal()

    }
}




rootProject.name = "ArduinoBluetooth"
include(":app")
 