package api.steps;

import api.clients.OrderClient;
import io.qameta.allure.Step;
import api.models.Ingredients;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;

public class OrderSteps {
    private OrderClient orderClient = new OrderClient();

    @Step("Создать заказ с авторизацией и ингредиентами")
    public Response createOrderWithAuth(Ingredients ingredients, String token) {
        return orderClient.create(ingredients, token);
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutAuth(Ingredients ingredients) {
        return orderClient.create(ingredients, null);
    }

    @Step("Создать список валидных ингредиентов")
    public Ingredients createValidIngredients() {
        return new Ingredients(Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",  // Краторная булка
                "61c0c5a71d1f82001bdaaa6f",  // Соус Spicy-X
                "61c0c5a71d1f82001bdaaa70"   // Мясо бессмертных моллюсков
        ));
    }

    @Step("Создать пустой список ингредиентов")
    public Ingredients createEmptyIngredients() {
        return new Ingredients(Collections.emptyList());
    }

    @Step("Создать список с неверным хешем ингредиента")
    public Ingredients createInvalidIngredients() {
        return new Ingredients(Arrays.asList("invalid123", "wrong456"));
    }
}