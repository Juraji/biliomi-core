import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.m2ci.msp.github-ivy-repo") version "0.1.1"
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.20"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.4.20"
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.spring") version "1.4.20"

}

group = "nl.juraji"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_14
java.targetCompatibility = JavaVersion.VERSION_14

val axon = "4.4.5"
val axonReactor = "4.4.2"
val reactorValidations = "master-SNAPSHOT"
val mockk = "1.10.2"
val springMockk = "3.0.0"
val testcontainers = "1.15.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.axonframework:axon-spring-boot-starter:$axon") {
        exclude("org.axonframework", "axon-server-connector")
    }
    implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter:$axonReactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.projectreactor.addons:reactor-extra")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor:reactor-tools")
    implementation("com.github.Juraji:reactor-validations:$reactorValidations")

    runtimeOnly("mysql:mysql-connector-java")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
        exclude("org.mockito", "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.axonframework:axon-test:$axon")
    testImplementation("io.mockk:mockk:$mockk")
    testImplementation("com.ninja-squad:springmockk:$springMockk")
    testImplementation("org.testcontainers:mysql:$testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainers")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = java.targetCompatibility.majorVersion
    }
}
