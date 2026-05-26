plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "com.hivehook"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates("com.hivehook", "sdk", "0.1.0")
    pom {
        name.set("Hivehook Java SDK")
        description.set("Official Java client for Hivehook, the self-hostable webhook gateway.")
        url.set("https://hivehook.com")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("hivehook")
                name.set("Hivehook")
                email.set("hello@hivehook.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/hivehook/sdk-java.git")
            developerConnection.set("scm:git:ssh://git@github.com/hivehook/sdk-java.git")
            url.set("https://github.com/hivehook/sdk-java")
        }
    }
}
