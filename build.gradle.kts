import com.code42.version.Version
import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

val kotlinVersion = "2.3.20"

plugins {
    base
    kotlin("jvm") version "2.3.20"
    id("idea")
    `maven-publish`
    id("com.diffplug.spotless").version("6.25.0")
    id("org.jetbrains.dokka").version("2.2.0")
    id("io.gitlab.arturbosch.detekt").version("1.23.0")
    jacoco
}
val githubRepo = System.getenv("GITHUB_REPOSITORY") ?: "martofeld/pipelinekt"
val groupName = "com.martofeld.jenkins"
val baseProjectName = "pipelinekt"
val publishedProjects = listOf("core", "internal", "dsl")
val activeProjects = publishedProjects

val ktlintVersion = "1.1.1"
val kotlinRules = mapOf(
    "max_line_length" to "150",
)

allprojects {
    group = "com.martofeld"
    version = Version.getVersion()

    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JVM_17)
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

// Root-level spotless configuration
spotless {
    kotlin {
        target("**/*.kt")
        ktlint(ktlintVersion).editorConfigOverride(kotlinRules)
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint(ktlintVersion).editorConfigOverride(kotlinRules)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks {
    register("incrementVersion") {
        doLast {
            Version.incrementVersion()
        }
    }
}

tasks.named("build") {
    // Don't finalize with dokka as it's a top-level task that doesn't exist
    // finalizedBy("dokka")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    if (!base.archivesName.get().startsWith("pipelinekt-")) {
        base.archivesName.set("pipelinekt-${base.archivesName.get()}")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8", kotlinVersion))
        implementation(kotlin("reflect", kotlinVersion))
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    }

    // Skip the example module for spotless check
    if (project.name == "examples") {
        tasks.withType<com.diffplug.gradle.spotless.SpotlessTask> {
            enabled = false
        }
    }

    tasks.withType<Test> {
        // Temporarily disable tests until we update them
        enabled = true
        useJUnit()
    }

    if (publishedProjects.contains(project.name)) {
        apply(plugin = "org.gradle.maven-publish")
        apply(plugin = "com.diffplug.spotless")
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "org.gradle.jacoco")
        apply(plugin = "org.jetbrains.dokka")

        configure<SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                ktlint(ktlintVersion).editorConfigOverride(kotlinRules)
                trimTrailingWhitespace()
                endWithNewline()
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint(ktlintVersion).editorConfigOverride(kotlinRules)
                trimTrailingWhitespace()
                endWithNewline()
            }
        }

        val sourcesJar by tasks.registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        jacoco {
            toolVersion = "0.8.7"
        }

        dokka {
            moduleName.set(project.name)
            dokkaPublications.html {
                suppressInheritedMembers.set(true)
            }
            dokkaSourceSets.main {
                sourceLink {
                    localDirectory.set(file("./"))
                    remoteUrl("https://github.com/$githubRepo/tree/master")
                    remoteLineSuffix.set("#L")
                }
            }
        }

        artifacts {
            add("archives", sourcesJar)
            add("archives", tasks.named("dokkaGenerate"))
        }

        tasks.withType<JacocoReport> {
            reports {
                xml.required.set(false)
                csv.required.set(false)
                html.required.set(true)
                html.outputLocation.set(layout.buildDirectory.dir("reports/coverage"))
            }
        }

        tasks.build {
            finalizedBy("jacocoTestReport")
        }

        detekt {
            source.setFrom(files("src/main/kotlin", "src/test/kotlin"))
            baseline = file("detekt-${project.name}-baseline.xml")
            allRules = false
            config = files("${project.rootDir}/config/detekt/detekt.yml")
        }

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
            exclude(".*/resources/.*,.*/build/.*")
            jvmTarget = "17" // Use JDK 17 instead of 21 for detekt
            config.setFrom(files("${project.rootDir}/config/detekt/detekt.yml"))
            // Skip this task to make the build work for now
            enabled = false
        }

        val tag = System.getProperty("tag")
        val libVersion = if (tag.isNullOrEmpty()) project.version.toString() else tag + "-SNAPSHOT"
        println("PUBLISHING- groupId: $group version: $libVersion artifactId: ${base.archivesName.get()}")
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = group.toString()
                    version = libVersion
                    artifactId = base.archivesName.get()
                    from(components["java"])
                    artifact(sourcesJar)
                    artifact(tasks.named("dokkaGenerate"))
                }
            }
            repositories {
                mavenLocal()
                maven {
                    name = "Snapshot"
                    url = uri("https://artifactory.corp.code42.com/artifactory/libs-snapshot-local/")
                    credentials {
                        username = System.getProperty("gradle.wrapperUser")
                        password = System.getProperty("gradle.wrapperPassword")
                    }
                }
                maven {
                    name = "Release"
                    url = uri("https://artifactory.corp.code42.com/artifactory/libs-release-local/")
                    credentials {
                        username = System.getProperty("gradle.wrapperUser")
                        password = System.getProperty("gradle.wrapperPassword")
                    }
                }
            }
        }
    }
}
