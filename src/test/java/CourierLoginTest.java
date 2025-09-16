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
         client.post("/api/v1/courier", courier);
     }

     @After
     public void tearDown() {
         if (courierId != 0) {
             client.delete("/api/v1/courier/" + courierId);
         }
     }

     @Test
     @DisplayName("Курьер может авторизоваться")
     public void courierCanLogin() {
         Response response = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), courier.getPassword(), null));

         response.then()
                 .statusCode(200)
                 .body("id", notNullValue());

         courierId = response.then().extract().path("id");
     }

     @Test
     @DisplayName("Логин с неправильным паролем возвращает ошибку")
     public void loginWithWrongPasswordReturnsError() {
         Response response = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), "wrong_password", null));

         response.then()
                 .statusCode(404)
                 .body("message", equalTo("Учетная запись не найдена"));

         Response loginResponse = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), courier.getPassword(), null));
         courierId = loginResponse.then().extract().path("id");
     }

     @Test
     @DisplayName("Логин без пароля возвращает ошибку")
     public void loginWithoutPasswordReturnsError() {
         Response response = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), null, null));

         response.then()
                 .statusCode(400)
                 .body("message", equalTo("Недостаточно данных для входа"));

         Response loginResponse = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), courier.getPassword(), null));
         courierId = loginResponse.then().extract().path("id");
     }
 }