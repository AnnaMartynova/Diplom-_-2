package api.tests.order;

import api.models.Ingredients;
import api.models.User;
import api.steps.OrderSteps;
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
public class OrderCreateTest {
    private OrderSteps orderSteps;
    private UserSteps userSteps;
    private String token;
    private User user;
    private Ingredients validIngredients;

    @Before
    public void setUp() {
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();

        String email = userSteps.generateUniqueEmail();
        user = userSteps.createTestUser(email);
        token = userSteps.createUserAndGetToken(user);

        validIngredients = orderSteps.createValidIngredients();
    }

    @After
    public void tearDown() {
        userSteps.deleteUser(token);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией: успешный сценарий")
    @Description("Проверка статуса 200 и всех полей ответа при создании заказа с авторизацией")
    public void createOrderWithAuthSuccess() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации: успешный сценарий")
    @Description("Проверка статуса 200 и всех полей ответа при создании заказа без авторизации")
    public void createOrderWithoutAuthSuccess() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов: ошибка 400")
    @Description("Проверка статуса 400 и сообщения об ошибке при пустом списке ингредиентов")
    public void createOrderWithoutIngredientsReturnsError() {
        Ingredients emptyIngredients = orderSteps.createEmptyIngredients();

        orderSteps.createOrderWithAuth(emptyIngredients, token)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента: ошибка 500")
    @Description("Проверка статуса 500 при передаче неверного хеша ингредиента")
    public void createOrderWithInvalidHashReturnsError() {
        Ingredients invalidIngredients = orderSteps.createInvalidIngredients();

        orderSteps.createOrderWithAuth(invalidIngredients, token)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}