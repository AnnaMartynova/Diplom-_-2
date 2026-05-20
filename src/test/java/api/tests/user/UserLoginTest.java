package api.tests.user;

import api.models.User;
import api.steps.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class UserLoginTest {
    private UserSteps userSteps;
    private User user;
    private String email;
    private String token;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        email = userSteps.generateUniqueEmail();
        user = userSteps.createTestUser(email);
        token = userSteps.createUserAndGetToken(user);
    }

    @After
    public void tearDown() {
        userSteps.deleteUser(token);
    }

    @Test
    @DisplayName("Успешный вход: проверка всех полей ответа")
    @Description("Проверка статуса 200 и всех полей ответа при успешном входе")
    public void loginSuccess() {
        userSteps.loginUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным email: ошибка 401")
    @Description("Проверка статуса 401 и сообщения об ошибке при неверном email")
    public void loginWithWrongEmailReturnsError() {
        User wrongEmail = new User("wrong" + email, user.getPassword(), null);

        userSteps.loginUser(wrongEmail)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем: ошибка 401")
    @Description("Проверка статуса 401 и сообщения об ошибке при неверном пароле")
    public void loginWithWrongPasswordReturnsError() {
        User wrongPassword = new User(email, "wrongpassword", null);

        userSteps.loginUser(wrongPassword)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с несуществующим пользователем: ошибка 401")
    @Description("Проверка статуса 401 и сообщения об ошибке для несуществующего пользователя")
    public void loginWithNonExistentUserReturnsError() {
        User nonExistentUser = new User(
                userSteps.generateUniqueEmail(),
                "password123",
                "NonExistent"
        );

        userSteps.loginUser(nonExistentUser)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}