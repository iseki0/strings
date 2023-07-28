import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("me.champeau.jmh") version "0.7.1"
}

group = "space.iseki.strings"
version = "0.1.0-SNAPSHOT"

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

tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "1.8"
}

