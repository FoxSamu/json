import dev.runefox.json.ObjectCodecTask

plugins {
    id("java")
    id("maven-publish")
}

group = "dev.runefox"
version = "0.7.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

tasks.named<JavaCompile>("compileJava") {
    // suppress all module warnings
    options.compilerArgs.add("-Xlint:-module")
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
