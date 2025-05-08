import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "2.1.0"
}

group = "ind.glowingstone.nuclear"
version = "1.0-SNAPSHOT"
tasks {
    withType<ShadowJar> {
        manifest {
            attributes["Main-Class"] = "ind.glowingstone.nuclear.MainKt"
        }
        mergeServiceFiles()
    }
}
repositories {
    mavenCentral()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "ind.glowingstone.nuclear.MainKt"
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-cio:2.3.0")
    implementation("io.ktor:ktor-client-serialization:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}