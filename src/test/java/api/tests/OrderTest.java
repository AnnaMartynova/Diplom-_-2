package api.tests;

import api.models.Ingredients;
import api.models.User;
import api.steps.OrderSteps;
import api.steps.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
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
    public void orderWithAuthReturnsStatus200() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Заказ с авторизацией: success = true")
    public void orderWithAuthReturnsSuccessTrue() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Заказ с авторизацией: возвращается name")
    public void orderWithAuthReturnsName() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Заказ с авторизацией: возвращается номер заказа")
    public void orderWithAuthReturnsOrderNumber() {
        orderSteps.createOrderWithAuth(validIngredients, token)
                .then()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Заказ без авторизации: статус 200")
    public void orderWithoutAuthReturnsStatus200() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Заказ без авторизации: success = true")
    public void orderWithoutAuthReturnsSuccessTrue() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Заказ без авторизации: возвращается name")
    public void orderWithoutAuthReturnsName() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Заказ без авторизации: возвращается номер заказа")
    public void orderWithoutAuthReturnsOrderNumber() {
        orderSteps.createOrderWithoutAuth(validIngredients)
                .then()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Заказ без ингредиентов: статус 400")
    public void orderWithoutIngredientsReturnsStatus400() {
        Ingredients emptyIngredients = orderSteps.createEmptyIngredients();

        orderSteps.createOrderWithAuth(emptyIngredients, token)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Заказ без ингредиентов: сообщение об ошибке")
    public void orderWithoutIngredientsReturnsErrorMessage() {
        Ingredients emptyIngredients = orderSteps.createEmptyIngredients();

        orderSteps.createOrderWithAuth(emptyIngredients, token)
                .then()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с неверным хешем: статус 500")
    public void orderWithInvalidHashReturnsStatus500() {
        Ingredients invalidIngredients = orderSteps.createInvalidIngredients();

        orderSteps.createOrderWithAuth(invalidIngredients, token)
                .then()
                .statusCode(500);
    }
}