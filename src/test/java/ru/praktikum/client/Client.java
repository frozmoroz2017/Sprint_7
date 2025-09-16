 package ru.praktikum.client;

 import io.qameta.allure.Step;
 import io.restassured.response.Response;
 import static io.restassured.RestAssured.given;

 public class Client {
     private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

     @Step("Send POST request to {endpoint}")
     public Response post(String endpoint, Object body) {
         return given()
                 .header("Content-type", "application/json")
                 .baseUri(BASE_URL)
                 .body(body)
                 .post(endpoint);
     }

     @Step("Send GET request to {endpoint}")
     public Response get(String endpoint) {
         return given()
                 .header("Content-type", "application/json")
                 .baseUri(BASE_URL)
                 .get(endpoint);
     }

     @Step("Send DELETE request to {endpoint}")
     public Response delete(String endpoint) {
         return given()
                 .header("Content-type", "application/json")
                 .baseUri(BASE_URL)
                 .delete(endpoint);
     }
 }