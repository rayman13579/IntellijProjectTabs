plugins {
    id("org.jetbrains.intellij.platform") version "2.5.0"
    kotlin("jvm")
}

group = "at.rayman"
version = "1.6"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IU", "2025.2.3")
    }
    implementation(kotlin("stdlib-jdk8"))
}

intellijPlatform {
    pluginConfiguration {
        name = "ProjectTabs"
    }
    pluginVerification {
        ides {
            select {
                sinceBuild = "252"
                untilBuild = ""
            }
        }
    }
    signing {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

kotlin {
    jvmToolchain(17)
}