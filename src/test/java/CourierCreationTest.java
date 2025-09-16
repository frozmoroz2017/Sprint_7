 package ru.praktikum.tests;

 import io.qameta.allure.junit4.DisplayName;
 import io.restassured.response.Response;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import ru.praktikum.client.Client;
 import ru.praktikum.model.Courier;

 import static org.hamcrest.Matchers.*;

 public class CourierCreationTest {
     private Client client;
     private Courier courier;
     private int courierId;

     @Before
     public void setUp() {
         client = new Client();
         courier = Courier.getRandom();
     }

     @After
     public void tearDown() {
         if (courierId != 0) {
             client.delete("/api/v1/courier/" + courierId);
         }
     }

     @Test
     @DisplayName("Курьера можно создать")
     public void courierCanBeCreated() {
         Response response = client.post("/api/v1/courier", courier);

         response.then()
                 .statusCode(201)
                 .body("ok", equalTo(true));

         Response loginResponse = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), courier.getPassword(), null));
         courierId = loginResponse.then().extract().path("id");
     }

     @Test
     @DisplayName("Нельзя создать двух одинаковых курьеров")
     public void cannotCreateDuplicateCourier() {
         client.post("/api/v1/courier", courier);

         Response response = client.post("/api/v1/courier", courier);

         response.then()
                 .statusCode(409)
                 .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

         Response loginResponse = client.post("/api/v1/courier/login",
                 new Courier(courier.getLogin(), courier.getPassword(), null));
         courierId = loginResponse.then().extract().path("id");
     }

     @Test
     @DisplayName("Создание без логина возвращает ошибку")
     public void creationWithoutLoginReturnsError() {
         Courier courierWithoutLogin = new Courier(null, courier.getPassword(), courier.getFirstName());

         Response response = client.post("/api/v1/courier", courierWithoutLogin);

         response.then()
                 .statusCode(400)
                 .body("message", equalTo("Недостаточно данных для создания учетной записи"));
     }

     @Test
     @DisplayName("Создание без пароля возвращает ошибку")
     public void creationWithoutPasswordReturnsError() {
         Courier courierWithoutPassword = new Courier(courier.getLogin(), null, courier.getFirstName());

         Response response = client.post("/api/v1/courier", courierWithoutPassword);

         response.then()
                 .statusCode(400)
                 .body("message", equalTo("Недостаточно данных для создания учетной записи"));
     }
 }