plugins {
    id("java")
    id("checkstyle")
    id("application")
    id("jacoco")
    id("org.sonarqube") version "6.2.0.5505"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application { mainClass = "hexlet.code.App" }

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.1.3")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("io.javalin:javalin-bundle:5.6.4")
    implementation("io.javalin:javalin-rendering:5.6.4")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.3.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)    // ☑️ ОБЯЗАТЕЛЬНО для SonarQube
        html.required.set(true)   // по желанию
    }
}

sonar {
    properties {
        property("sonar.projectKey", "kalaysolay_java-project-72")
        property("sonar.organization", "kalaysolay")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}