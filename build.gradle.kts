plugins {
    id("java")
    id("java-library-distribution")
}

/** Группа текущего пакета */
group = "ru.astecom"

/** Версия текущего пакета */
version = "1.0-SNAPSHOT"

/** Версия пакета ml4j */
var ml4jVersion = "1.0.0-M2.1";

/** Платформа пакета ml4j */
var ml4jPlatform = "nd4j-native";

/** Версия библиотеки slf4j */
var slf4jVersion = "2.0.5";

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.deeplearning4j:deeplearning4j-core:$ml4jVersion") {
        // Плагины нам не нужны
        exclude(group = "com.twelvemonkeys.imageio", module = "imageio-jpeg")
        exclude(group = "com.twelvemonkeys.imageio", module = "imageio-tiff")
        exclude(group = "com.twelvemonkeys.imageio", module = "imageio-psd")
    }
    implementation(group = "org.nd4j", name = ml4jPlatform, version = ml4jVersion)
    implementation(group = "org.nd4j", name = ml4jPlatform, version = ml4jVersion, classifier = "linux-x86_64")
    implementation("org.bytedeco:javacv:1.5.7")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
}

distributions {
    main {
        distributionBaseName = "ml-sandbox"
        contents {
            from("models") {
                into("models");
            }
            from("Readme.md")
            exclude { it.name.contains(Regex("(macosx|windows|ios|android)")) }
        }
    }
}

tasks {
    jar {
        dependsOn("train")
        manifest {
            attributes["Main-Class"] = "ru.astecom.Sandbox"
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { "lib/${it.name}" }
        }
    }

    test {
        useJUnitPlatform()
    }

    register("train", JavaExec::class) {
        group = "Execution"
        description = "Обучение иющихся моделей, для дальнейшего использования"
        classpath = sourceSets["main"].runtimeClasspath;
        mainClass = "ru.astecom.Trainer"
    }

    register("run", JavaExec::class) {
        group = "Execution"
        description = "Запуск песочницы"
        classpath = sourceSets["main"].runtimeClasspath;
        mainClass = "ru.astecom.Sandbox"
    }
}