package org.example.api;

import io.restassured.response.Response;
import org.example.model.User;

import static io.restassured.RestAssured.given;

public class UserApi extends BaseApi {

    final static String REGISTER = "/api/auth/register";
    final static String LOGIN = "/api/auth/login";
    final static String USER_DATA = "/api/auth/user";
    final static String DELETE = "/api/auth/user";

    //Создание пользователя
    public Response userRegister(User user) {
        return given(requestSpecification)
                .body(user)
                .when()
                .post(REGISTER);
    }

    //Авторизация и регистрация
    public Response userLogin(User user, String token) {
        return given(requestSpecification)
                .header("Authorization", "Bearer "+ token)
                .body(user)
                .when()
                .post(LOGIN);
    }

    //Обновление информации о пользователе
    public Response patchUserData(User user, String token) {
        return given(requestSpecification)
                .header("Authorization", "Bearer "+ token)
                .body(user)
                .when()
                .patch(USER_DATA);
    }

    //Удаление пользователя
    public void userDelete(String token) {
        given(requestSpecification)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(DELETE);
    }

    //Записать AccessToken в строковую переменную (jwt токен начинается с 7 индекса)
    public String getUserAccessToken(Response response) {
        String bearerToken = response.then().extract().path("accessToken");
        return bearerToken.substring(7);
    }
}
