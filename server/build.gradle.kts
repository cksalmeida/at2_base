plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "com.fatec.at2_base"
version = "1.0.0"
application {
    mainClass.set("com.fatec.at2_base.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverCors)
    implementation(libs.ktor.serverStatusPages)
    implementation(libs.ktor.serializationJson)
    implementation(libs.kotlinx.serializationJson)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.ktor.serializationJson)
    testImplementation(libs.ktor.clientContentNegotiation)
    testImplementation(libs.kotlin.testJunit)
}