plugins {
    kotlin("jvm") version "1.9.0"
    id("me.champeau.jmh") version "0.7.1"
    jacoco
    signing
    `maven-publish`
}

group = "space.iseki.strings"
version = "0.2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:13.0")
    testImplementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    jmh(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}

fun JavaToolchainSpec.configure() {
    languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    javaCompiler.set(javaToolchains.compilerFor { configure() })
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "Central"
            url = if (version.toString().endsWith("SNAPSHOT")) {
                // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                uri("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            }
            credentials {
                username = properties["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = properties["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("strings")
                description.set("Strings util")
                url.set("https://github.com/iseki0/strings")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("iseki0")
                        name.set("iseki zero")
                        email.set("iseki@iseki.space")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/iseki0/strings.git")
                    url.set("https://github.com/iseki0/strings")
                }
            }
        }
    }
}

afterEvaluate {
    signing {
        // To use local gpg command, configure gpg options in ~/.gradle/gradle.properties
        // reference: https://docs.gradle.org/current/userguide/signing_plugin.html#example_configure_the_gnupgsignatory
        useGpgCmd()
        publishing.publications.forEach { sign(it) }
    }
}
