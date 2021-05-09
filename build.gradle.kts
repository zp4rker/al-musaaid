plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"

    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.zp4rker"
version = "1.1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.zp4rker:discore:2.4.0")
    implementation("com.sedmelluq:lavaplayer:1.3.61")
    implementation("org.kohsuke:github-api:1.117")
    implementation("com.google.cloud:google-cloud-speech:1.26.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.zp4rker.persistant.PersistantKt"
        attributes["Implementation-Version"] = project.version
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn("shadowJar")
}