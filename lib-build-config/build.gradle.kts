plugins {
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

group = "com.allthingsandroid.buildkconfigplugin"
version = "0.1.0"

gradlePlugin {
    plugins {
        register("buildKConfigPlugin") {
            id = "com.allthingsandroid.buildk-desktop"
            version = "0.1.0-dev"
            implementationClass = "com.allthingsandroid.kmp.plugin.BuildKConfigPlugin" // The fully-qualified class name of your plugin implementation
        }
    }
}

sourceSets {
    test {
        java {
            srcDirs("src/main/test/")
        }
    }
}


dependencies {

    //compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    compileOnly("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:1.7.3")
    compileOnly("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.1.0")
    //implementation(kotlin("stdlib-jdk8"))

    // Coroutines for main implementation
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Coroutines support for testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3") // JUnit 5
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3") // JUnit engine
    testImplementation("io.kotest:kotest-assertions-core:6.0.0.M1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


publishing {

    publications {
        create<MavenPublication>("gradlePlugin") {
            from(components["java"]) // Use the Java component (since Gradle plugins are compiled into a JAR)
            groupId = "com.allthingsandroid"
            artifactId = "buildkconfig-desktop"
            version = "0.1.0-dev"
        }
    }

    repositories {
        mavenLocal() // Publishes to ~/.m2/repository
    }
}