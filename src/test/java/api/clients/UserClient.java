package api.clients;

import api.models.User;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {
    private static final String REGISTER = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";
    private static final String USER = "/api/auth/user";

    public Response create(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER);
    }

    public Response login(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(LOGIN);
    }

    public Response delete(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .delete(USER);
    }
}