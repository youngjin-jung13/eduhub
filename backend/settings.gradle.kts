plugins {
    // 로컬에 JDK 21이 없으면 Gradle 툴체인이 자동으로 내려받을 수 있게 한다.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "eduhub"
