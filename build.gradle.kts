import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("com.h2database:h2:2.1.214")
	implementation("com.mysql:mysql-connector-j")

	// Add JWT dependencies in your build.gradle or build.gradle.kts
//	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
//	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
//	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

//	implementation("org.springframework.boot:spring-boot-starter-security")

//	implementation("com.h2database:h2:1.4.200")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
