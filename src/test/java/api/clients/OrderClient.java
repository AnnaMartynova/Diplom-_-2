package api.clients;

import api.models.Ingredients;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {
    private static final String ORDERS = "/api/orders";

    public Response create(Ingredients ingredients, String token) {
        var request = given()
                .spec(getBaseSpec())
                .body(ingredients);

        // Добавляем header только если token не null
        if (token != null && !token.isEmpty()) {
            request.header("Authorization", token);
        }

        return request.when().post(ORDERS);
    }
}