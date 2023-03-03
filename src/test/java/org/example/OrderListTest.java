package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.model.Order;
import org.example.model.User;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest extends BaseTest {

    /** Тестовые данные */
    //Данные пользователя
    static User userJohn = new User("john@mail.ru", "password", "Джон");

    //Создание пользователя вне тестового класса, для возможности извлечения из него accessToken,
    //для последующего использования в тестах и удаления пользователя
    static Response johnData = userApi.userRegister(userJohn);

    //Данные заказов
    static Order order1 = new Order(List.of(orderApi.getAvailableIngredients().get(0), orderApi.getAvailableIngredients().get(4),
            orderApi.getAvailableIngredients().get(7), orderApi.getAvailableIngredients().get(10)));
    static Order order2 = new Order(List.of(orderApi.getAvailableIngredients().get(1), orderApi.getAvailableIngredients().get(3)));
    static Order order3 = new Order(List.of(orderApi.getAvailableIngredients().get(0), orderApi.getAvailableIngredients().get(1),
            orderApi.getAvailableIngredients().get(10), orderApi.getAvailableIngredients().get(4), orderApi.getAvailableIngredients().get(9)));

    @AfterClass
    public static void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление пользователя
        userApi.userDelete(userApi.getUserAccessToken(johnData));
    }

    @Test
    @DisplayName("Успешное получение списка заказов авторизованного пользователя")
    @Description("Проверить, что возможно вернуть список заказов авторизованного пользователя")
    public void checkThatItsPossibleToReceiveOrdersOfAuthorizedUser() {
        //Создать три заказа пользователем Джон
        Response orderResponse1 = orderApi.orderCreateAuthorizedUser(order1, userApi.getUserAccessToken(johnData));
        Response orderResponse2 = orderApi.orderCreateAuthorizedUser(order2, userApi.getUserAccessToken(johnData));
        Response orderResponse3 = orderApi.orderCreateAuthorizedUser(order3, userApi.getUserAccessToken(johnData));
        //Получить заказы пользователя
        Response ordersResponse = orderApi.getUserOrder(userApi.getUserAccessToken(johnData));
        //Проверить, что вернулся правильный ответ и статус код
        ordersResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("orders[0]._id", equalTo(orderApi.getOrderId(orderResponse1)))
                .body("orders[0].ingredients", equalTo(List.of(orderApi.getAvailableIngredients().get(0), orderApi.getAvailableIngredients().get(4),
                        orderApi.getAvailableIngredients().get(7), orderApi.getAvailableIngredients().get(10))))
                .body("orders[0].name", equalTo("Spicy традиционный-галактический флюоресцентный минеральный бургер"))
                .body("orders[1]._id", equalTo(orderApi.getOrderId(orderResponse2)))
                .body("orders[1].ingredients", equalTo(List.of(orderApi.getAvailableIngredients().get(1), orderApi.getAvailableIngredients().get(3))))
                .body("orders[1].name", equalTo("Бессмертный био-марсианский бургер"))
                .body("orders[2]._id", equalTo(orderApi.getOrderId(orderResponse3)))
                .body("orders[2].ingredients", equalTo(List.of(orderApi.getAvailableIngredients().get(0), orderApi.getAvailableIngredients().get(1),
                        orderApi.getAvailableIngredients().get(10), orderApi.getAvailableIngredients().get(4), orderApi.getAvailableIngredients().get(9))))
                .body("orders[2].name", equalTo("Минеральный флюоресцентный антарианский spicy бессмертный бургер"))
                .body("total", notNullValue())
                .body("totalToday", notNullValue())
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Неуспешное получение списка заказов неавторизованного пользователя")
    @Description("Проверить, что невозможно вернуть список заказов неавторизованного пользователя")
    public void checkThatItsImpossibleToReceiveOrdersOfUnauthorizedUser() {
        //Создать заказ неавторизованным пользователем
        Response orderResponse1 = orderApi.orderCreateUnauthorizedUser(order1);
        //Получить заказ пользователя
        Response ordersResponse = orderApi.getUserOrder("");
        //Проверить, что вернулся правильный ответ и статус код
        ordersResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .statusCode(SC_UNAUTHORIZED);
    }
}