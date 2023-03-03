package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.model.Order;
import org.example.model.User;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest extends BaseTest {

    /** Тестовые данные */
    //Данные пользователя
    static User userJack = new User("jack@mail.ru", "password", "Джек");

    //Создание пользователя вне тестового класса, для возможности извлечения из него accessToken,
    //для последующего использования в тестах и удаления пользователя
    static Response jackData = userApi.userRegister(userJack);

    //Данные заказов
    static Order order = new Order(List.of(orderApi.getAvailableIngredients().get(0), orderApi.getAvailableIngredients().get(4),
                                        orderApi.getAvailableIngredients().get(7), orderApi.getAvailableIngredients().get(10)));
    static Order orderWithNoIngredients = new Order();
    static Order orderWithWrongHashOfIngredient = new Order(List.of("14jackascanlakjcbs134k1qsck21aac", orderApi.getAvailableIngredients().get(4)));


    @AfterClass
    public static void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление пользователя
        userApi.userDelete(userApi.getUserAccessToken(jackData));
    }

    @Test
    @DisplayName("Успешное создание заказа авторизованным пользователем")
    @Description("Проверить, что авторизованный пользователь может успешно создать заказ из существующих ингредиентов")
    public void checkThatAuthorizedUserCouldCreateOrder() {
        //Создать заказ
        Response orderResponse = orderApi.orderCreateAuthorizedUser(order, userApi.getUserAccessToken(jackData));
        //Проверить, что вернулся правильный ответ и статус код
        orderResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("name", equalTo("Spicy традиционный-галактический флюоресцентный минеральный бургер"))
                .body("order.ingredients[0]._id", equalTo(orderApi.getAvailableIngredients().get(0)))
                .body("order.ingredients[1]._id", equalTo(orderApi.getAvailableIngredients().get(4)))
                .body("order.ingredients[2]._id", equalTo(orderApi.getAvailableIngredients().get(7)))
                .body("order.ingredients[3]._id", equalTo(orderApi.getAvailableIngredients().get(10)))
                .body("order._id", notNullValue())
                .body("order.owner.name", equalTo(userJack.getName()))
                .body("order.owner.email", equalTo(userJack.getEmail()))
                .body("order.status", equalTo("done"))
                .body("order.number", notNullValue())
                .body("order.price", equalTo(orderApi.getPriceOfIngredient(0) +
                        orderApi.getPriceOfIngredient(4) + orderApi.getPriceOfIngredient(7) +
                        orderApi.getPriceOfIngredient(10)))
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Успешное создание заказа неавторизованным пользователем")
    @Description("Проверить, что неавторизованный пользователь может успешно создать заказ из существующих ингредиентов")
    public void checkThatUnauthorizedUserCouldCreateOrder() {
        //Создать заказ
        Response orderResponse = orderApi.orderCreateUnauthorizedUser(order);
        //Проверить, что вернулся правильный ответ и статус код
        orderResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("name", equalTo("Spicy традиционный-галактический флюоресцентный минеральный бургер"))
                .body("order.number", notNullValue())
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов")
    @Description("Проверить, что пользователь не может создать заказ без указания ингредиентов")
    public void checkThatUserCouldNotCreateOrderWithoutIngredients() {
        //Создать заказ
        Response orderResponse = orderApi.orderCreateUnauthorizedUser(orderWithNoIngredients);
        //Проверить, что вернулся правильный ответ и статус код
        orderResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингридиентов")
    @Description("Проверить, что пользователь не может создать заказ с указанием неправильного хеш-кода хотя бы одного из ингредиентов")
    public void checkThatUserCouldNotCreateOrderWithWrongHashOfIngredients() {
        //Создать заказ
        Response orderResponse = orderApi.orderCreateUnauthorizedUser(orderWithWrongHashOfIngredient);
        //Проверить, что вернулся правильный статус код
        orderResponse.then().assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
