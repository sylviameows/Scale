plugins {
    kotlin("jvm") version "2.1.20"
}

group = "net.sylviameows"
version = "3.0.0"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "sylviameows"
        url = uri("https://repo.sylviameo.ws/releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("net.sylviameows:kitti:0.2.0")
}

kotlin {
    jvmToolchain(21)
}