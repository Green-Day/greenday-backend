rootProject.name = "recyclica-backend"

val kotlinVersion: String by settings
val shadowVersion: String by settings

pluginManagement.resolutionStrategy.eachPlugin {
    if(requested.id.id.startsWith("org.jetbrains.kotlin"))
        useVersion(kotlinVersion)
    if(requested.id.id == "com.github.johnrengelman.shadow")
        useVersion(shadowVersion)
}
