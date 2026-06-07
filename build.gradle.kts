plugins {
    id("java")
    // Плагин для интеграции Allure с Gradle
    id("io.qameta.allure") version "2.11.2"
}

group = "seminars.qa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Настройка версии Allure
allure {
    version.set("2.24.0")
}

dependencies {
    // JUnit 5 для запуска тестов
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // REST Assured для API тестирования
    testImplementation("io.rest-assured:rest-assured:5.3.2")

    // Jackson для сериализации/десериализации JSON
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // Allure адаптер для REST Assured (чтобы запросы и ответы логировались в отчет)
    testImplementation("io.qameta.allure:allure-rest-assured:2.24.0")
}

tasks.test {
    useJUnitPlatform()
    // Указываем кодировку, чтобы в отчетах русский язык отображался корректно
    systemProperty("file.encoding", "UTF-8")
}