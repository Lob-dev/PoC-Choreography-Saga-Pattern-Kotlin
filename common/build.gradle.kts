import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "com.study"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val springBootVersion = "2.7.5"
val awsSpringCloudVersion = "2.4.2"
val springCloudVersion = "3.1.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("io.awspring.cloud:spring-cloud-aws-dependencies:$awsSpringCloudVersion")
    implementation("io.awspring.cloud:spring-cloud-starter-aws-messaging:$awsSpringCloudVersion")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$springCloudVersion")
    implementation("io.github.openfeign:feign-okhttp:11.10")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}