package apiTests;

import POJO.Booking;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import config.TestConfigBookingApi;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class BookingWithAuthTests extends TestConfigBookingApi {

    @Test
    public void noAuth() {

        Booking booking = new Booking();

        given()
                .when()
                .body(booking)
                .put("booking/1")
                .then()
                .statusCode(403)
                .contentType(equalTo("text/plain; charset=utf-8"))
                .body(equalTo("Forbidden"))
                .log().all();
    }

    @Test
    public void putWithAuth() {

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .when().post( "auth");

        String token = response.jsonPath().getString("token");

        Booking booking = new Booking();

        given()
                .header("Cookie", "token=" + token)
                .when()
                .body(booking)
                .put("booking/1")
                .then()
                .statusCode(200);
    }
}
