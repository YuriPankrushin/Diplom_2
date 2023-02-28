package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.model.User;
import org.junit.AfterClass;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class UserCreateTest extends BaseTest {

    /** Тестовые данные */
    //Данные пользователя
    static User userBen = new User("ben@mail.ru", "password", "Бен");
    static User userJoe = new User("joe@mail.ru", "password");

    //Создание пользователя вне тестового класса, для возможности извлечения из него accessToken,
    //для последующего удаления пользователя
    static Response benData = userApi.userRegister(userBen);

    @AfterClass
    public static void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление пользователя
        userApi.userDelete(userApi.getUserAccessToken(benData));
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    @Description("Успешно создать пользователя с указанием email, пароля и имени")
    public void checkThatUserCouldBeRegistered() {
        //Проверить, что вернулся правильный ответ и статус код
        benData.then().assertThat().body("success", equalTo(true))
                .body("$", hasKey("accessToken"))
                .body("$", hasKey("refreshToken"))
                .body("user.email", equalTo(userBen.getEmail()))
                .body("user.name", equalTo(userBen.getName()))
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание пользователя с данными существующего пользователя")
    @Description("Проверить, что невозможно создать пользователя с email, паролем и именем имеющегося пользователя")
    public void checkThatUserCouldNotBeRegisteredWithExistingUserData() {
        //Создать нового пользователя с данными существующего
        Response newUserWithBenData = userApi.userRegister(userBen);
        //Проверить, что вернулся правильный ответ и статус код
        newUserWithBenData.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверить, что невозможно создать пользователя без указания имени")
    public void checkThatUserCouldNotBeRegisteredWithoutName() {
        //Создать нового пользователя с данными существующего
        Response joeData = userApi.userRegister(userJoe);
        //Проверить, что вернулся правильный ответ и статус код
        joeData.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(SC_FORBIDDEN);
    }
}