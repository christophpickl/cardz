repositories {
    jcenter()
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.3.70"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.itextpdf:itextpdf:5.5.13.1")
}
