package api.tests;

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
public class OrderTest {
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
    @DisplayName("Заказ с авторизацией: успешное создание")
    @Description("Проверка, что при создании заказа с авторизацией возвращается статус 200, success = true, name и номер заказа")
    public void orderWithAuthReturnsSuccessResponse() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Заказ без авторизации: успешное создание")
    @Description("Проверка, что при создании заказа без авторизации возвращается статус 200, success = true, name и номер заказа")
    public void orderWithoutAuthReturnsSuccessResponse() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Заказ без ингредиентов: ошибка 400")
    @Description("Проверка, что при создании заказа без ингредиентов возвращается статус 400 и сообщение об ошибке")
    public void orderWithoutIngredientsReturnsError() {
        Ingredients emptyIngredients = orderSteps.createEmptyIngredients();

        orderSteps.createOrderWithAuth(emptyIngredients, token)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с неверным хешем ингредиента: ошибка 500")
    @Description("Проверка, что при создании заказа с неверным хешем ингредиента возвращается статус 500")
    public void orderWithInvalidHashReturnsError() {
        Ingredients invalidIngredients = orderSteps.createInvalidIngredients();

        orderSteps.createOrderWithAuth(invalidIngredients, token)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}