package org.example.api;

import io.restassured.response.Response;
import org.example.model.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseApi {

    final static String CREATE = "/api/orders";
    final static String GET_USER_ORDER = "/api/orders";
    final static String GET_ALL_INGREDIENTS = "/api/ingredients";

    //Создание заказа без авторизации
    public Response orderCreateUnauthorizedUser(Order order) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(CREATE);
    }

    //Создание заказа с авторизацией
    public Response orderCreateAuthorizedUser(Order order, String token) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+ token)
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

    public List<String> getAvailableIngredients () {
        Response response = given(requestSpecification)
                .header("Content-type", "application/json")
                .when()
                .get(GET_ALL_INGREDIENTS);
        return response.then().extract().path("data._id");
    }

    public int getPriceOfIngredient(int ingredientIndex){
        Response response = given(requestSpecification)
                .header("Content-type", "application/json")
                .when()
                .get(GET_ALL_INGREDIENTS);
        return response.then().extract().path(String.format("data.price[%s]", ingredientIndex));
    }
}
