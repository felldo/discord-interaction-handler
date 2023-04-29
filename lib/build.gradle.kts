plugins {
    `java-library`
    `maven-publish`
    signing
    id("com.github.ben-manes.versions") version "0.46.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}


java {
    withJavadocJar()
    withSourcesJar()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        create<MavenPublication>("DIH") {
            artifactId = "dih"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Discord Interaction Handler")
                description.set(rootProject.description)
                url.set("https://github.com/KILLEliteMaste/discord-interaction-handler")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                inceptionYear.set("2022")
                developers {
                    developer {
                        id.set("KILLEliteMaste")
                        name.set("Dominic Fellbaum")
                        email.set("d.fellbaum@hotmail.de")
                        url.set("https://github.com/KILLEliteMaste")
                        timezone.set("Europe/Berlin")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/KILLEliteMaste/discord-interaction-handler.git")
                    developerConnection.set("scm:git:git@github.com:KILLEliteMaste/discord-interaction-handler.git")
                    url.set("https://github.com/KILLEliteMaste/discord-interaction-handler")
                }
            }
        }
    }
    repositories {
        maven {

            val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")
            name = "OSSRH"
            url = if (isReleaseVersion) {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            } else {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
            credentials {
                username = findProperty("NEXUS_USERNAME") as String
                password = findProperty("NEXUS_PASSWORD") as String
            }
        }
    }
}

signing {
    val signingKey = findProperty("DIH_SINGING_SECRET_KEY_RING_FILE") as String
    val signingKeyId = findProperty("DIH_SIGNING_KEY_ID") as String
    val signingPassword = findProperty("DIH_SIGNING_PASSWORD") as String

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["DIH"])
}

/*
tasks.withType(Javadoc::class.java) {
    isFailOnError = true
}*/

tasks.javadoc {
    isFailOnError = false
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
