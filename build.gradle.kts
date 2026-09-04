plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
}