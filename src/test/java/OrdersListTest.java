 package ru.praktikum.tests;

 import io.qameta.allure.junit4.DisplayName;
 import io.restassured.response.Response;
 import org.junit.Test;
 import ru.praktikum.client.Client;

 import java.util.List;

 import static org.hamcrest.Matchers.*;

 public class OrdersListTest {
     private Client client = new Client();

     @Test
     @DisplayName("Получение списка заказов")
     public void canGetOrdersList() {
         Response response = client.get("/api/v1/orders");

         response.then()
                 .statusCode(200)
                 .body("orders", notNullValue())
                 .body("orders", instanceOf(List.class));
     }

     @Test
     @DisplayName("Получение списка заказов с лимитом")
     public void canGetOrdersListWithLimit() {
         Response response = client.get("/api/v1/orders?limit=5");

         response.then()
                 .statusCode(200)
                 .body("orders", notNullValue())
                 .body("orders.size()", lessThanOrEqualTo(5));
     }
 }