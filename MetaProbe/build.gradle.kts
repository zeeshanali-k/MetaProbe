@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)

    alias(libs.plugins.com.vanniktech.maven.publish)
}

dependencies {

    implementation(libs.ktor.core)
    implementation(libs.jsoup)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}