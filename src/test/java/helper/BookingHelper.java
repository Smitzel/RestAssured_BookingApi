package helper;

import POJO.Booking;
import io.restassured.response.Response;
import utils.TestData;

import static io.restassured.RestAssured.given;
import static utils.TestVariableManager.GetVariable;
import static utils.TestVariableManager.SetVariable;
import static utils.TestVariables.*;

public class BookingHelper {

    public static void createBooking() {
        Booking booking = new Booking();
        Response response = given()
                .body(booking)
                .when()
                .post("booking");
        response.then().statusCode(200);
        SetVariable(POST_ADDITIONALNEEDS, response.jsonPath().getString("booking.additionalneeds"));
        SetVariable(POST_BOOKINGID, response.jsonPath().getString("bookingid"));
        SetVariable(POST_CHECKIN, response.jsonPath().getString("booking.bookingdates.checkin"));
        SetVariable(POST_CHECKOUT, response.jsonPath().getString("booking.bookingdates.checkout"));
        SetVariable(POST_DEPOSITPAID, response.jsonPath().getString("booking.depositpaid"));
        SetVariable(POST_FIRSTNAME, response.jsonPath().getString("booking.firstname"));
        SetVariable(POST_LASTNAME, response.jsonPath().getString("booking.lastname"));
        SetVariable(POST_TOTALPRICE, response.jsonPath().getString("booking.totalprice"));
    }

    public static void getBooking(int expectedStatusCode) {
        Response response = given()
                .when()
                .get("booking/{id}", GetVariable(POST_BOOKINGID));
        response.then().statusCode(expectedStatusCode);

        switch (expectedStatusCode) {
            case 200:
                SetVariable(GET_ADDITIONALNEEDS, response.jsonPath().getString("additionalneeds"));
                SetVariable(GET_CHECKIN, response.jsonPath().getString("bookingdates.checkin"));
                SetVariable(GET_CHECKOUT, response.jsonPath().getString("bookingdates.checkout"));
                SetVariable(GET_DEPOSITPAID, response.jsonPath().getString("depositpaid"));
                SetVariable(GET_FIRSTNAME, response.jsonPath().getString("firstname"));
                SetVariable(GET_LASTNAME, response.jsonPath().getString("lastname"));
                SetVariable(GET_TOTALPRICE, response.jsonPath().getString("totalprice"));
                break;
            case 404:
                System.out.println("Status 404: Booking not found");
                break;
            default:
                System.out.println("Unexpected statuscode: " + expectedStatusCode);
                throw new IllegalArgumentException("Unexpected statuscode " + expectedStatusCode);
        }
    }

    public static void patchBooking() {
        String newName = TestData.getFirstName();
        SetVariable(PATCH_FIRSTNAME, newName);
        String patchedName = "{\"firstname\":\"" + newName + "\"}";

        given()
                .cookie("token", GetVariable(AUTH_TOKEN))
                .when()
                .body(patchedName)
                .patch("booking/" + (GetVariable(POST_BOOKINGID)))
                .then()
                .statusCode(200);
    }

    public static void putBooking() {
        Booking booking = new Booking();
        SetVariable(PUT_ADDITIONALNEEDS, booking.getAdditionalneeds());
        SetVariable(PUT_CHECKIN, booking.getBookingdates().getCheckin());
        SetVariable(PUT_CHECKOUT, booking.getBookingdates().getCheckout());
        SetVariable(PUT_DEPOSITPAID, String.valueOf(booking.isDepositpaid()));
        SetVariable(PUT_FIRSTNAME, booking.getFirstname());
        SetVariable(PUT_LASTNAME, booking.getLastname());
        SetVariable(PUT_TOTALPRICE, String.valueOf(booking.getTotalprice()));

        given()
                .body(booking)
                .when()
                .cookie("token", GetVariable(AUTH_TOKEN))
                .put("booking/" + GetVariable(POST_BOOKINGID))
                .then()
                .statusCode(200);
    }

            public static void deleteBooking() {
                given()
                        .cookie("token", GetVariable(AUTH_TOKEN))
                        .when()
                        .delete("booking/" + GetVariable(POST_BOOKINGID))
                        .then()
                        .statusCode(201);
        }

}
