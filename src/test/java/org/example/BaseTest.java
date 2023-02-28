package org.example;

import org.example.api.OrderApi;
import org.example.api.UserApi;

public class BaseTest {
    static OrderApi orderApi = new OrderApi();
    static UserApi userApi = new UserApi();
}
