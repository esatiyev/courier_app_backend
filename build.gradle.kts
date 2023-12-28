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
	implementation("com.mysql:mysql-connector-j")

	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
	implementation("org.springframework.boot:spring-boot-starter-security")

	testImplementation("org.springframework.security:spring-security-test")




//	implementation("org.springframework.boot:spring-boot-starter-validation")
//	implementation("org.springframework.boot:spring-boot-starter-thymeleaf") // If using Thymeleaf

//	implementation("org.springframework.security:spring-security-config")
//	implementation("org.springframework.security:spring-security-web")
//	implementation("org.springframework.security:spring-security-core")

//	implementation("org.springframework.boot:spring-boot-starter-security")


	// JWT
//	implementation("io.jsonwebtoken:jjwt:0.9.1")

//	implementation("org.springframework.security:spring-security-web")
//	implementation("org.springframework.security:spring-security-config")
//
//	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
//	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
//	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")





}
ext["spring-security.version"]= "6.2.1"
ext["spring.version"]= "6.1.2"
tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}
tasks.withType<Test> {
	useJUnitPlatform()
}
