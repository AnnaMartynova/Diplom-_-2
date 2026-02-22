package api.clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

public class BaseClient {
    protected static final String BASE_URL = "https://stellarburgers.education-services.ru";

    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .addFilter(new AllureRestAssured())
                .build();
    }
}