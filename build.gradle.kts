import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.5"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.allopen") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "com.poc.saga"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

val SpringBootVersion = "2.7.5"
val awsSpringCloudVersion = "2.4.2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:$SpringBootVersion")
	implementation("io.awspring.cloud:spring-cloud-aws-dependencies:$awsSpringCloudVersion")
	implementation("io.awspring.cloud:spring-cloud-starter-aws-messaging:$awsSpringCloudVersion")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

	testImplementation("org.springframework.boot:spring-boot-starter-test:$SpringBootVersion")
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