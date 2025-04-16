plugins {
    id("org.jetbrains.intellij.platform") version "2.5.0"
    kotlin("jvm")
}

group = "at.rayman"
version = "2.0-beta"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2025.1")
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
                sinceBuild = "243"
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
        channels.set(listOf("beta"))
    }
}

kotlin {
    jvmToolchain(17)
}
