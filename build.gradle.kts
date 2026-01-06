/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

import dev.runefox.json.ObjectCodecTask

plugins {
    id("java")
    id("maven-publish")
}

group = "dev.runefox"
version = "0.8"

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
        if ("rfxMavenUser" in properties && "rfxMavenPass" in properties) {
            maven {
                name = "RunefoxMaven"
                url = uri("https://mvn.runefox.dev/releases")
                credentials {
                    username = properties["rfxMavenUser"].toString()
                    password = properties["rfxMavenPass"].toString()
                }
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
