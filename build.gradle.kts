val projectVersion: String by project
val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val postgreSqlVersion: String by project
val scryptVersion: String by project
val jodaTimeVersion: String by project
val jodaJacksonVersion: String by project

plugins {
    application
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

group = "xyz.greenday.backend"
version = projectVersion

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://dl.bintray.com/kotlin/exposed") }
    maven { url = uri("http://repo.pearx.net/maven2/develop/") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("ch.qos.logback:logback-classic:$logbackVersion")


    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-freemarker:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgreSqlVersion")

    implementation("com.lambdaworks:scrypt:$scryptVersion")

    implementation("joda-time:joda-time:$jodaTimeVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:$jodaJacksonVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
            useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}