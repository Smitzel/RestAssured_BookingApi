package apiTests;

import POJO.Booking;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import config.TestConfigBookingApi;
import utils.TestData;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;


public class BookingTests extends TestConfigBookingApi {

    @Test
    public void healthCheck() {
        given()
                .log().all()
                .when()
                .get("ping")
                .then()
                .statusCode(201);
    }

    @Test
    public void createBookingWithFixedData() {
        given()
                .body("{" +
                        "\"firstname\":\"Jim\"," +
                        "\"lastname\":\"Brown\"," +
                        "\"totalprice\":111," +
                        "\"depositpaid\":true," +
                        "\"bookingdates\":{\"checkin\":\"2018-01-01\",\"checkout\":\"2019-01-01\"}," +
                        "\"additionalneeds\":\"Breakfast\"}")
                .when()
                .post("booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .log().all();
    }

    @Test
    public void createBookingWithRandomData() {
        given()
                .body("{" +
                        "\"firstname\":\"" + TestData.getFirstName() + "\"," +
                        "\"lastname\":\"" + TestData.getLastName() + "\"," +
                        "\"totalprice\":\"" + TestData.getPrice(1, 9999) + "\"," +
                        "\"depositpaid\":\"" + TestData.getRandomBoolEan() + "\"," +
                        "\"bookingdates\":{\"checkin\":\"" + TestData.getCheckInDate() + "\",\"checkout\":\"" + TestData.getCheckOutDate() + "\"}," +
                        "\"additionalneeds\":\"" + TestData.getRandomNeeds() + "\"}")
                .when()
                .post("booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .log().all();
    }

    @Test
    public void createBookingWithPOJO() {
        Booking booking = new Booking();
        given()
                .body(booking)
                .when()
                .post("booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .log().all();
    }

    @Test
    public void createBookingWithPOJOWithUpdateData() {
        Booking booking = new Booking();
        String additionalNeeds = "Breakfast at Tiffany's";
        booking.setAdditionalneeds(additionalNeeds);

        given()
                .body(booking)
                .when()
                .post("booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("booking.additionalneeds", equalTo(additionalNeeds))
                .log().all();
    }

    @Test
    public void createAndReadBookingWithPOJO() {
        Booking booking = new Booking();
        Response response = given()
                .body(booking)
                .when()
                .post("booking");

        String bookingId = response.jsonPath().getString("bookingid");
        String firstname = response.jsonPath().getString("booking.firstname");
        String lastname = response.jsonPath().getString("booking.lastname");
        String depositpaid = response.jsonPath().getString("booking.depositpaid");

        given()
                .when()
                .get("booking/" + bookingId)
                .then()
                .assertThat().body("firstname", equalTo(firstname))
                .assertThat().body("lastname", equalTo(lastname))
                .assertThat().body("depositpaid", equalTo(Boolean.valueOf(depositpaid)))
                .log().all();

    }

}
