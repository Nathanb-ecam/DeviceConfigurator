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
        //maven { url = uri("https://maven.taktik.be/content/groups/public") }
        google()
        mavenCentral()
    }
}

rootProject.name = "ArduinoBluetooth"
include(":app")
 