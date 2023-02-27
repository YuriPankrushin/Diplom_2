package org.example.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseApi {
    public RequestSpecification requestSpecification =
            RestAssured.given()
                    .baseUri("https://stellarburgers.nomoreparties.site/");
}