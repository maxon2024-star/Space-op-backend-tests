package seminars.qa;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import seminars.qa.dto.AddSatelliteRequest;
import seminars.qa.dto.MissionRequest;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@Epic("Управление космическими операциями")
@Feature("Space Operations API")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpaceOperationApiTest {

    // Убрали final, чтобы можно было задать уникальные имена при старте
    private static String CONSTELLATION_NAME;
    private static String SATELLITE_NAME;

    @BeforeAll
    public static void setup() {
        // Генерируем уникальные имена для каждого прогона тестов (например: Allure-Orbit-a1b2c3d4)
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CONSTELLATION_NAME = "Allure-Orbit-" + uniqueId;
        SATELLITE_NAME = "Allure-Sat-" + uniqueId;

        // Базовые настройки для всех запросов
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
        RestAssured.basePath = "/api";

        // Добавляем логирование в консоль и прикрепляем Allure фильтр
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Test
    @Order(1)
    @Story("Управление группировками")
    @Description("Успешное создание новой спутниковой группировки")
    public void testCreateConstellation() {
        given()
                .queryParam("name", CONSTELLATION_NAME)
                .when()
                .post("/constellations")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    @Story("Управление спутниками")
    @Description("Успешное добавление спутника связи в существующую группировку")
    public void testAddSatellite() {
        Map<String, Object> param = Map.of(
                "type", "COMMUNICATION",
                "name", SATELLITE_NAME,
                "batteryLevel", 100.0,
                "bandwidth", 500.0
        );
        AddSatelliteRequest request = new AddSatelliteRequest(CONSTELLATION_NAME, param);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/add-satellites")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    @Story("Выполнение миссий")
    @Description("Успешный запуск миссии для одиночного спутника")
    public void testExecuteMissionForSingleSatellite() {
        MissionRequest request = new MissionRequest("SINGLE_SATELLITE", CONSTELLATION_NAME, SATELLITE_NAME);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/missions")
                .then()
                .statusCode(200)
                .body(containsString("Миссия для одиночного спутника"));
    }

    @Test
    @Order(4)
    @Story("Мониторинг")
    @Description("Получение общей сводки системы")
    public void testGetOverview() {
        given()
                .when()
                .get("/overview")
                .then()
                .statusCode(200)
                .body(containsString(CONSTELLATION_NAME));
    }

    @Test
    @Order(5)
    @Story("Управление спутниками")
    @Description("Вывод спутника из эксплуатации (удаление)")
    public void testDecommissionSatellite() {
        given()
                .pathParam("constellationName", CONSTELLATION_NAME)
                .pathParam("satelliteName", SATELLITE_NAME)
                .when()
                .delete("/constellations/{constellationName}/satellites/{satelliteName}")
                .then()
                .statusCode(204);
    }
}