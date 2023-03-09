package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.model.User;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Random;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class UserChangeDataTest extends BaseTest {
    /** Тестовые данные */
    //Данные пользователя
    static Random random = new Random();
    static User userAuthorized = new User("box" + random.nextInt(10000000) + "@yandex.ru", "password", "user" + random.nextInt(10000000));
    static User userAuthorizedUpdatedData = new User("box" + random.nextInt(10000000) + "@yandex.ru", null, "user" + random.nextInt(10000000));
    static User userUnauthorized = new User("box" + random.nextInt(10000000) + "@yandex.ru", "password", "user" + random.nextInt(10000000));


    //Создание пользователя вне тестового класса, для возможности извлечения из него accessToken,
    //для последующего использования в тестах и удаления пользователя
    static Response authorizedData = userApi.userRegister(userAuthorized);

    @AfterClass
    public static void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление пользователя
        userApi.userDelete(userApi.getUserAccessToken(authorizedData));
    }

    @Test
    @DisplayName("Успешное изменение почты и имени авторизованного пользователя")
    @Description("Проверить, что авторизованный пользователь может успешно изменить свои данные: электронную почту и имя пользователя")
    public void checkThatAuthorizedUserCouldChangeOwnEmailAndUserName() {
        //Изменить почту и имя пользователя
        Response authorizedUserDataChangeResponse = userApi.patchUserData(userAuthorizedUpdatedData, userApi.getUserAccessToken(authorizedData));
        //Проверить, что вернулся правильный ответ и статус код
        authorizedUserDataChangeResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(userAuthorizedUpdatedData.getEmail()))
                .body("user.name", equalTo(userAuthorizedUpdatedData.getName()))
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Попытка изменения почты и имени неавторизованного пользователя")
    @Description("Проверить, что неавторизованный пользователь не может изменить свои данные")
    public void checkThatUnauthorizedUserCouldNotChangeOwnData() {
        //Изменить почту и имя пользователя
        Response unauthorizedUserDataChangeResponse = userApi.patchUserData(userUnauthorized, "");
        //Проверить, что вернулся правильный ответ и статус код
        unauthorizedUserDataChangeResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .statusCode(SC_UNAUTHORIZED);
    }
}