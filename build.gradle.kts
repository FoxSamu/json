import dev.runefox.json.ObjectCodecTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm") version "1.9.20-Beta2"
}

group = "dev.runefox"
version = "0.7"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    // Only compile main sources with kotlin, we're not running them
    compileOnly(kotlin("stdlib-jdk8"))
    // Test sources do run kotlin
    testImplementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = "json"
            version = "${project.version}"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "ShwMaven"
            url = uri("https://maven.shadew.net/")
            credentials {
                username = properties["shwMavenUser"].toString()
                password = properties["shwMavenPass"].toString()
            }
        }
    }
}

tasks.register<ObjectCodecTask>("generateObjectCodec") {
    maxParams = 16
    pkg = "dev.runefox.json.codec"
    out = file("${rootDir}/src/main/java")
}

tasks.compileJava {
    dependsOn("generateObjectCodec")
}


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}
