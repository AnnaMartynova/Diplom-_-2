package api.tests;

import api.models.User;
import api.steps.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class UserTest {
    private UserSteps userSteps;
    private User user;
    private String email;
    private String token;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        email = userSteps.generateUniqueEmail();
        user = userSteps.createTestUser(email);
    }

    @After
    public void tearDown() {
        userSteps.deleteUser(token);
    }

    @Test
    @DisplayName("Создание пользователя: возвращается статус 200")
    public void createUserReturnsStatus200() {
        ValidatableResponse response = userSteps.createUser(user).then();
        token = response.extract().path("accessToken");

        response.statusCode(200);
    }

    @Test
    @DisplayName("Создание пользователя: success = true")
    public void createUserReturnsSuccessTrue() {
        ValidatableResponse response = userSteps.createUser(user).then();
        token = response.extract().path("accessToken");

        response.body("success", is(true));
    }

    @Test
    @DisplayName("Создание пользователя: возвращается email")
    public void createUserReturnsEmail() {
        ValidatableResponse response = userSteps.createUser(user).then();
        token = response.extract().path("accessToken");

        response.body("user.email", equalTo(email.toLowerCase()));
    }

    @Test
    @DisplayName("Создание пользователя: возвращается name")
    public void createUserReturnsName() {
        ValidatableResponse response = userSteps.createUser(user).then();
        token = response.extract().path("accessToken");

        response.body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Создание пользователя: возвращается accessToken")
    public void createUserReturnsAccessToken() {
        ValidatableResponse response = userSteps.createUser(user).then();
        token = response.extract().path("accessToken");

        response.body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя: возвращается refreshToken")
    public void createUserReturnsRefreshToken() {
        ValidatableResponse response = userSteps.createUser(user).then();
        token = response.extract().path("accessToken");

        response.body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание дубликата: статус 403")
    public void createDuplicateUserReturnsStatus403() {
        userSteps.createUser(user).then().statusCode(200);

        userSteps.createDuplicateUser(user)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание дубликата: сообщение об ошибке")
    public void createDuplicateUserReturnsErrorMessage() {
        userSteps.createUser(user).then().statusCode(200);

        userSteps.createDuplicateUser(user)
                .then()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание без email: статус 403")
    public void createUserWithoutEmailReturnsStatus403() {
        User userNoEmail = new User(null, "password123", "TestUser");

        userSteps.createUserWithMissingField(userNoEmail)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание без email: сообщение об ошибке")
    public void createUserWithoutEmailReturnsErrorMessage() {
        User userNoEmail = new User(null, "password123", "TestUser");

        userSteps.createUserWithMissingField(userNoEmail)
                .then()
                .body("message", containsString("required fields"));
    }

    @Test
    @DisplayName("Создание без password: статус 403")
    public void createUserWithoutPasswordReturnsStatus403() {
        User userNoPassword = new User(email, null, "TestUser");

        userSteps.createUserWithMissingField(userNoPassword)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание без password: сообщение об ошибке")
    public void createUserWithoutPasswordReturnsErrorMessage() {
        User userNoPassword = new User(email, null, "TestUser");

        userSteps.createUserWithMissingField(userNoPassword)
                .then()
                .body("message", containsString("required fields"));
    }

    @Test
    @DisplayName("Создание без name: статус 403")
    public void createUserWithoutNameReturnsStatus403() {
        User userNoName = new User(email, "password123", null);

        userSteps.createUserWithMissingField(userNoName)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание без name: сообщение об ошибке")
    public void createUserWithoutNameReturnsErrorMessage() {
        User userNoName = new User(email, "password123", null);

        userSteps.createUserWithMissingField(userNoName)
                .then()
                .body("message", containsString("required fields"));
    }

    @Test
    @DisplayName("Успешный вход: статус 200")
    public void loginReturnsStatus200() {
        token = userSteps.createUserAndGetToken(user);

        userSteps.loginUser(user)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Успешный вход: success = true")
    public void loginReturnsSuccessTrue() {
        token = userSteps.createUserAndGetToken(user);

        userSteps.loginUser(user)
                .then()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Успешный вход: возвращается email")
    public void loginReturnsEmail() {
        token = userSteps.createUserAndGetToken(user);

        userSteps.loginUser(user)
                .then()
                .body("user.email", equalTo(email.toLowerCase()));
    }

    @Test
    @DisplayName("Успешный вход: возвращается name")
    public void loginReturnsName() {
        token = userSteps.createUserAndGetToken(user);

        userSteps.loginUser(user)
                .then()
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Успешный вход: возвращается accessToken")
    public void loginReturnsAccessToken() {
        token = userSteps.createUserAndGetToken(user);

        userSteps.loginUser(user)
                .then()
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Успешный вход: возвращается refreshToken")
    public void loginReturnsRefreshToken() {
        token = userSteps.createUserAndGetToken(user);

        userSteps.loginUser(user)
                .then()
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным email: статус 401")
    public void loginWithWrongEmailReturnsStatus401() {
        token = userSteps.createUserAndGetToken(user);
        User wrongEmail = new User("wrong" + email, user.getPassword(), null);

        userSteps.loginUserWithWrongCredentials(wrongEmail)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Вход с неверным email: сообщение об ошибке")
    public void loginWithWrongEmailReturnsErrorMessage() {
        token = userSteps.createUserAndGetToken(user);
        User wrongEmail = new User("wrong" + email, user.getPassword(), null);

        userSteps.loginUserWithWrongCredentials(wrongEmail)
                .then()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем: статус 401")
    public void loginWithWrongPasswordReturnsStatus401() {
        token = userSteps.createUserAndGetToken(user);
        User wrongPassword = new User(email, "wrongpassword", null);

        userSteps.loginUserWithWrongCredentials(wrongPassword)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Вход с неверным паролем: сообщение об ошибке")
    public void loginWithWrongPasswordReturnsErrorMessage() {
        token = userSteps.createUserAndGetToken(user);
        User wrongPassword = new User(email, "wrongpassword", null);

        userSteps.loginUserWithWrongCredentials(wrongPassword)
                .then()
                .body("message", equalTo("email or password are incorrect"));
    }
}