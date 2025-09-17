package ru.praktikum.tests;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.client.Client;
import ru.praktikum.model.Courier;

import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private Client client;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        client = new Client();
        courier = Courier.getRandom();
        // Создаем курьера перед тестом
        client.post("/api/v1/courier", courier)
                .then()
                .statusCode(201);
    }

    @After
    public void tearDown() {
        // Логин и удаление курьера, даже если тест упал
        try {
            Response loginResponse = client.post("/api/v1/courier/login",
                    new Courier(courier.getLogin(), courier.getPassword(), null));

            if (loginResponse.statusCode() == 200) {
                courierId = loginResponse.then().extract().path("id");
                client.delete("/api/v1/courier/" + courierId);
            }
        } catch (Exception e) {
            System.out.println("Courier login failed in tearDown: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void courierCanLogin() {
        Response response = client.post("/api/v1/courier/login",
                new Courier(courier.getLogin(), courier.getPassword(), null));

        // Сначала получаем ID, потом проверяем ответ
        courierId = response.then().extract().path("id");

        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин с неправильным паролем возвращает ошибку")
    public void loginWithWrongPasswordReturnsError() {
        Response response = client.post("/api/v1/courier/login",
                new Courier(courier.getLogin(), "wrong_password", null));

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин без пароля возвращает ошибку")
    public void loginWithoutPasswordReturnsError() {
        Response response = client.post("/api/v1/courier/login",
                new Courier(courier.getLogin(), null, null));

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без логина возвращает ошибку")
    public void loginWithoutLoginReturnsError() {
        Response response = client.post("/api/v1/courier/login",
                new Courier(null, courier.getPassword(), null));

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с неправильным логином возвращает ошибку")
    public void loginWithWrongLoginReturnsError() {
        Response response = client.post("/api/v1/courier/login",
                new Courier("wrong_login", courier.getPassword(), null));

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин несуществующего курьера возвращает ошибку")
    public void loginNonExistentCourierReturnsError() {
        Courier nonExistentCourier = Courier.getRandom();

        Response response = client.post("/api/v1/courier/login",
                new Courier(nonExistentCourier.getLogin(), nonExistentCourier.getPassword(), null));

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}