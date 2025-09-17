package ru.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.client.Client;
import ru.praktikum.model.Order;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationTest {
    private Client client;
    private List<String> colors;
    private Order order;

    public OrderCreationTest(List<String> colors) {
        this.colors = colors;
    }

    @Before
    public void setUp() {
        client = new Client();
        order = Order.getRandom(colors);
    }

    @After
    public void tearDown() {
        // Создаем заказ чтобы получить track для отмены
        try {
            Response createResponse = client.post("/api/v1/orders", order);

            if (createResponse.statusCode() == 201) {
                Integer trackId = createResponse.then().extract().path("track");
                // Отменяем заказ
                client.put("/api/v1/orders/cancel?track=" + trackId);
            }
        } catch (Exception e) {
            System.out.println("Failed to create or cancel order in tearDown: " + e.getMessage());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        });
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    public void orderCanBeCreatedWithDifferentColors() {
        Response response = client.post("/api/v1/orders", order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}