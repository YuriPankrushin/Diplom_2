package org.example.api;

import io.restassured.response.Response;
import org.example.model.Order;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseApi {

    final static String CREATE = "/api/orders";
    final static String GET_USER_ORDER = "/api/orders";

    //Создание заказа
    public Response orderCreate(Order order) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(CREATE);
    }

    //Получить заказы конкретного пользователя
    public Response getUserOrder(String token) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+ token)
                .when()
                .get(GET_USER_ORDER);
    }
}
