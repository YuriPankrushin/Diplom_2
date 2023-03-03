package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.model.User;
import org.junit.AfterClass;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class UserLoginTest extends BaseTest {

    /** Тестовые данные */
    //Данные пользователя
    static User userTed = new User("ted@mail.ru", "password", "Тэд");
    static User userTedWrongLogin = new User("wrong@mail.ru", "password");
    static User userTedWrongPassword = new User("ted@mail.ru", "WrongPassword!");

    //Создание пользователя вне тестового класса, для возможности извлечения из него accessToken,
    //для последующего использования в тестах и удаления пользователя
    static Response tedData = userApi.userRegister(userTed);

    @AfterClass
    public static void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление пользователя
        userApi.userDelete(userApi.getUserAccessToken(tedData));
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    @Description("Успешно авторизоваться пользователем с указанием существующих email, пароля, имени и токена")
    public void checkThatUserCouldLogin() {
        //Авторизация пользователя
        Response tedLoginResponse = userApi.userLogin(userTed, userApi.getUserAccessToken(tedData));
        //Проверить, что вернулся правильный ответ и статус код
        tedLoginResponse.then().assertThat().body("success", equalTo(true))
                .body("$", hasKey("accessToken"))
                .body("$", hasKey("refreshToken"))
                .body("user.email", equalTo(userTed.getEmail()))
                .body("user.name", equalTo(userTed.getName()))
                .statusCode(SC_OK);
    }


    @Test
    @DisplayName("Авторизация пользователя без указания логина")
    @Description("Проверить, что пользователь не сможет авторизоваться без указания логина")
    public void checkThatUserCouldNotLoginWithoutLoginMentioning() {
        //Авторизация пользователя
        Response tedLoginResponse = userApi.userLogin(userTedWrongLogin, userApi.getUserAccessToken(tedData));
        // Проверить, что вернулся правильный ответ и статус код
        tedLoginResponse.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Авторизация пользователя без указания пароля")
    @Description("Проверить, что пользователь не сможет авторизоваться без указания пароля")
    public void checkThatUserCouldNotLoginWithoutPasswordMentioning() {
        //Авторизация пользователя
        Response tedLoginResponse = userApi.userLogin(userTedWrongPassword, userApi.getUserAccessToken(tedData));
        //Проверить, что вернулся правильный ответ и статус код
        tedLoginResponse.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(SC_UNAUTHORIZED);
    }
}
