package api.steps;

import api.clients.UserClient;
import io.qameta.allure.Step;
import api.models.User;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.SC_OK;

public class UserSteps {
    private UserClient userClient = new UserClient();

    @Step("Создать пользователя с email: {user.email}")
    public Response createUser(User user) {
        return userClient.create(user);
    }

    @Step("Создать пользователя и получить токен: {user.email}")
    public String createUserAndGetToken(User user) {
        return userClient.create(user)
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("accessToken");
    }

    @Step("Создать уже существующего пользователя: {user.email}")
    public Response createDuplicateUser(User user) {
        return userClient.create(user);
    }

    @Step("Создать пользователя с отсутствующим полем")
    public Response createUserWithMissingField(User user) {
        return userClient.create(user);
    }

    @Step("Выполнить вход пользователя: {user.email}")
    public Response loginUser(User user) {
        return userClient.login(user);
    }

    @Step("Выполнить вход с неверными данными: {user.email}")
    public Response loginUserWithWrongCredentials(User user) {
        return userClient.login(user);
    }

    @Step("Удалить пользователя с токеном: {token}")
    public void deleteUser(String token) {
        if (token != null && !token.isEmpty()) {
            userClient.delete(token);
        }
    }

    @Step("Сгенерировать уникальный email")
    public String generateUniqueEmail() {
        return "testuser" + System.currentTimeMillis() + "@yandex.ru";
    }

    @Step("Создать тестового пользователя с email: {email}")
    public User createTestUser(String email) {
        return new User(email, "password123", "TestUser");
    }
}