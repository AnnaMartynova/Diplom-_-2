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

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class UserCreateTest {
    private UserSteps userSteps;
    private List<String> tokensToDelete;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        tokensToDelete = new ArrayList<>();
    }

    @After
    public void tearDown() {
        // Удаляем всех созданных пользователей
        for (String token : tokensToDelete) {
            userSteps.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Создание пользователя: успешный сценарий")
    @Description("Проверка статуса 200 и всех полей ответа при успешном создании пользователя")
    public void createUserSuccess() {
        String email = userSteps.generateUniqueEmail();
        User user = userSteps.createTestUser(email);

        var response = userSteps.createUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        String token = response.extract().path("accessToken");
        tokensToDelete.add(token);  // Сохраняем токен для удаления
    }

    @Test
    @DisplayName("Создание дубликата пользователя: ошибка 403")
    @Description("Проверка, что нельзя создать пользователя с уже существующим email")
    public void createDuplicateUserReturnsError() {
        // Создаем первого пользователя
        String email = userSteps.generateUniqueEmail();
        User user = userSteps.createTestUser(email);

        var createResponse = userSteps.createUser(user)
                .then()
                .statusCode(SC_OK);

        String firstUserToken = createResponse.extract().path("accessToken");
        tokensToDelete.add(firstUserToken);  // Сохраняем токен первого пользователя

        // Пытаемся создать дубликат
        userSteps.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email: ошибка 403")
    @Description("Проверка обязательности поля email")
    public void createUserWithoutEmailReturnsError() {
        User userNoEmail = new User(null, "password123", "TestUser");

        userSteps.createUser(userNoEmail)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));

        // Пользователь не создан, токен не добавляем
    }

    @Test
    @DisplayName("Создание пользователя без password: ошибка 403")
    @Description("Проверка обязательности поля password")
    public void createUserWithoutPasswordReturnsError() {
        String email = userSteps.generateUniqueEmail();
        User userNoPassword = new User(email, null, "TestUser");

        userSteps.createUser(userNoPassword)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));

        // Пользователь не создан, токен не добавляем
    }

    @Test
    @DisplayName("Создание пользователя без name: ошибка 403")
    @Description("Проверка обязательности поля name")
    public void createUserWithoutNameReturnsError() {
        String email = userSteps.generateUniqueEmail();
        User userNoName = new User(email, "password123", null);

        userSteps.createUser(userNoName)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));

        // Пользователь не создан, токен не добавляем
    }
}