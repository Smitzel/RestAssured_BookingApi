package apiTests;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingWithCSVTests {

    // Endpoint URL
    private static final String BASE_URL = "https://restful-booker.herokuapp.com/booking";

    @ParameterizedTest
    @CsvFileSource(resources = "/booking_testdata.csv", numLinesToSkip = 1)
//    numLinesToSkip will skip the first line/header
    void testPostBooking(String firstname, String lastname, Integer totalprice, Boolean depositpaid,
                         String checkin, String checkout, String additionalneeds) {
        // Create JSON-body
        String jsonBody = String.format(
                "{" +
                        "\"firstname\": \"%s\"," +
                        "\"lastname\": \"%s\"," +
                        "\"totalprice\": \"%d\"," +
                        "\"depositpaid\": \"%b\"," +
                        "\"bookingdates\": {\"checkin\": \"%s\", \"checkout\": \"%s\"}," +
                        "\"additionalneeds\": \"%s\"" +
                        "}",
                firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds != null ? additionalneeds : "");

        // Execute POST request
        Response response = given()
                .contentType("application/json")
                .body(jsonBody).log().all()
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(200).log().all()
                .extract()
                .response();

        // Check if the name in the response matches
        String returnedFirstname = response.jsonPath().getString("booking.firstname");
        assertEquals(firstname, returnedFirstname, "Firstname doesn't match!");
    }
}
