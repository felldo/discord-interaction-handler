plugins {
    `java-library`
    `maven-publish`
    signing
}


group = "net.fellbaum"
version = "1.0"
description = "A Java library for conveniently working with interactions from Discord"


repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    implementation("org.javacord:javacord:3.6.0-SNAPSHOT")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
    implementation("org.apache.logging.log4j:log4j-api:2.18.0")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}


java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
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
                url.set("https://github.com/KILLEliteMaste/DIH")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
                    connection.set("scm:git:https://github.com/KILLEliteMaste/DIH.git")
                    developerConnection.set("scm:git:git@github.com:KILLEliteMaste/DIH.git")
                    url.set("https://github.com/KILLEliteMaste/DIH")
                }
            }
        }
    }
    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
            val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
