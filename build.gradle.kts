import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val spigotVersion = "1.16.5-R0.1-SNAPSHOT"
val kotlinbukkitapiVersion = "0.2.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "pro.darc.raid"
version = "0.1.0"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
    mavenCentral()
}

dependencies {
    val changing = Action<ExternalModuleDependency> { isChanging = true }

    compileOnly("org.spigotmc:spigot-api:$spigotVersion")
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:core:$kotlinbukkitapiVersion") // core
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:serialization:$kotlinbukkitapiVersion", changing)
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:architecture:0.1.0-SNAPSHOT", changing)
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:plugins:$kotlinbukkitapiVersion", changing)
    implementation("org.yaml:snakeyaml:1.28")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

tasks.processResources {
    expand(
        "plugin_name" to project.name,
        "plugin_version" to project.version,
        "plugin_main" to "${project.group}.${project.name}"
    )
}

tasks.shadowJar {
    archiveClassifier.set("")
    dependencies {
        include(dependency("org.yaml:snakeyaml:1.28"))
    }
}

// System.setProperty("socksProxyHost","127.0.0.1")
// System.setProperty("socksProxyPort","1080")
