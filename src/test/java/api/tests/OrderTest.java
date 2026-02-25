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
    @DisplayName("Заказ с авторизацией: статус 200")
    @Description("Проверка, что при создании заказа с авторизацией возвращается статус 200")
    public void orderWithAuthReturnsStatus200() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Заказ с авторизацией: success = true")
    @Description("Проверка, что при создании заказа с авторизацией возвращается success = true")
    public void orderWithAuthReturnsSuccessTrue() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Заказ с авторизацией: возвращается name")
    @Description("Проверка, что при создании заказа с авторизацией возвращается название заказа")
    public void orderWithAuthReturnsName() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(SC_OK)
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Заказ с авторизацией: возвращается номер заказа")
    @Description("Проверка, что при создании заказа с авторизацией возвращается номер заказа")
    public void orderWithAuthReturnsOrderNumber() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(SC_OK)
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Заказ без авторизации: статус 200")
    @Description("Проверка, что при создании заказа без авторизации возвращается статус 200")
    public void orderWithoutAuthReturnsStatus200() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Заказ без авторизации: success = true")
    @Description("Проверка, что при создании заказа без авторизации возвращается success = true")
    public void orderWithoutAuthReturnsSuccessTrue() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Заказ без авторизации: возвращается name")
    @Description("Проверка, что при создании заказа без авторизации возвращается название заказа")
    public void orderWithoutAuthReturnsName() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(SC_OK)
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Заказ без авторизации: возвращается номер заказа")
    @Description("Проверка, что при создании заказа без авторизации возвращается номер заказа")
    public void orderWithoutAuthReturnsOrderNumber() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(SC_OK)
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Заказ без ингредиентов: статус 400")
    @Description("Проверка, что при создании заказа без ингредиентов возвращается статус 400")
    public void orderWithoutIngredientsReturnsStatus400() {
        Ingredients emptyIngredients = orderSteps.createEmptyIngredients();

        orderSteps.createOrderWithAuth(emptyIngredients, token)
                .then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Заказ без ингредиентов: сообщение об ошибке")
    @Description("Проверка, что при создании заказа без ингредиентов возвращается правильное сообщение об ошибке")
    public void orderWithoutIngredientsReturnsErrorMessage() {
        Ingredients emptyIngredients = orderSteps.createEmptyIngredients();

        orderSteps.createOrderWithAuth(emptyIngredients, token)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с неверным хешем: статус 500")
    @Description("Проверка, что при создании заказа с неверным хешем ингредиента возвращается статус 500")
    public void orderWithInvalidHashReturnsStatus500() {
        Ingredients invalidIngredients = orderSteps.createInvalidIngredients();

        orderSteps.createOrderWithAuth(invalidIngredients, token)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}