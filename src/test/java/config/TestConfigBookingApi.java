package config;

import io.restassured.RestAssured;
import io.restassured.config.*;
import org.junit.jupiter.api.BeforeAll;

public class TestConfigBookingApi {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";

        RestAssured.requestSpecification = RestAssured.given()
                .header("Content-Type", "application/json");

        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000)
                        .setParam("http.socket.timeout", 5000))
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails())
                .decoderConfig(DecoderConfig.decoderConfig()
                        .defaultContentCharset("UTF-8"));
    }
}
