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
    private Integer trackId;

    public OrderCreationTest(List<String> colors) {
        this.colors = colors;
    }

    @Before
    public void setUp() {
        client = new Client();
        trackId = null;
    }

    @After
    public void tearDown() {
        // Отменяем заказ после теста, если он был создан
        if (trackId != null) {
            try {
                client.put("/api/v1/orders/cancel?track=" + trackId);
            } catch (Exception e) {
                System.out.println("Failed to cancel order with track: " + trackId + ", error: " + e.getMessage());
            }
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
        Order order = Order.getRandom(colors);

        Response response = client.post("/api/v1/orders", order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        // Сохраняем track ID для отмены в tearDown
        trackId = response.then().extract().path("track");
    }
}