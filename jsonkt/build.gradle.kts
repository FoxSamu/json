import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm") version "1.9.20-Beta2"
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(kotlin("stdlib-jdk8"))
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
            artifactId = "jsonkt"
            version = "${project.version}"

            from(components["java"])
        }
    }
    repositories {
        if ("rfxMavenUser" in properties && "rfxMavenPass" in properties) {
            maven {
                name = "SamuRepo"
                url = uri("https://maven.runefox.dev/releases")
                credentials {
                    username = properties["rfxMavenUser"].toString()
                    password = properties["rfxMavenPass"].toString()
                }
            }
        }
    }
}


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}
