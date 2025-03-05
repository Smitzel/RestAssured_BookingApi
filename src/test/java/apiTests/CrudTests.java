package apiTests;

import POJO.Booking;
import config.TestConfigBookingApi;
import helper.AuthHelper;
import helper.BookingHelper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.TestData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.TestVariableManager.GetVariable;
import static utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.*;

public class CrudTests extends TestConfigBookingApi {

    @Test
    public void CRUDBooking() {
        //    create booking and store id
        Booking booking = new Booking();
        System.out.println("Generated Booking Data: " + booking);
        String firstNamePost = booking.getFirstname();
        String lastNamePost = booking.getLastname();
        Response postBooking = given()
                .body(booking)
                .when()
                .post("booking");
        int bookingId = postBooking.jsonPath().getInt("bookingid");

        //    READ the booking with the bookingId and validate the booking
        Response getBooking = given()
                .when()
                .get("booking/" + bookingId);

        String firstNameGet = getBooking.jsonPath().getString("firstname");
        String lastNameGet = getBooking.jsonPath().getString("lastname");

        assertEquals(firstNamePost, firstNameGet);
        assertEquals(lastNamePost, lastNameGet);

        //    Get Token
        Response getToken = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .when().post("auth");

        String token = getToken.jsonPath().getString("token");


        //    PATCH the firstname of the booking
        String newName = TestData.getFirstName();
        String patchedName = "{\"firstname\":\"" + newName + "\"}";

        given()
                .header("Cookie", "token=" + token)
                .when()
                .body(patchedName)
                .patch("booking/" + bookingId)
                .then()
                .statusCode(200);

        //    READ the booking with and validate the new firstname
        Response getBookingAfterPatch = given()
                .when()
                .get("booking/" + bookingId);

        String firstNamePatched = getBookingAfterPatch.jsonPath().getString("firstname");
        assertEquals(newName, firstNamePatched);


        //  PUT the booking and update all fields
        Booking updateBooking = new Booking();
        String updateFirstNamePost = updateBooking.getFirstname();
        String updateLastNamePost = updateBooking.getLastname();
        given()
                .header("Cookie", "token=" + token)
                .when()
                .body(updateBooking)
                .put("booking/" + bookingId);

        //    READ the updated booking with the bookingId and validate the booking
        Response getUpdateBooking = given()
                .when()
                .get("booking/" + bookingId);

        String firstNameGetUpdate = getUpdateBooking.jsonPath().getString("firstname");
        String lastNameGetUpdate = getUpdateBooking.jsonPath().getString("lastname");

        assertEquals(firstNameGetUpdate, updateFirstNamePost);
        assertEquals(lastNameGetUpdate, updateLastNamePost);

        //    DELETE the booking
        given()
                .header("Cookie", "token=" + token)
                .when()
                .delete("booking/" + bookingId)
                .then()
                .statusCode(201);

        //    READ the booking with the bookingId, to see if it is deleted
        given()
                .when()
                .get("booking/" + bookingId)
                .then()
                .contentType(equalTo("text/plain; charset=utf-8"))
                .body(equalTo("Not Found"));
    }

    @Test
    public void CRUDBookingWithHelper() {
        BookingHelper.createBooking();
        BookingHelper.getBooking(200);

        assertEquals(GetVariable(POST_FIRSTNAME), GetVariable(GET_FIRSTNAME));
        assertEquals(GetVariable(POST_LASTNAME), GetVariable(GET_LASTNAME));
        assertEquals(GetVariable(POST_TOTALPRICE), GetVariable(GET_TOTALPRICE));
        assertEquals(GetVariable(POST_DEPOSITPAID), GetVariable(GET_DEPOSITPAID));
        assertEquals(GetVariable(POST_CHECKIN), GetVariable(GET_CHECKIN));
        assertEquals(GetVariable(POST_CHECKOUT), GetVariable(GET_CHECKOUT));
        assertEquals(GetVariable(POST_ADDITIONALNEEDS), GetVariable(GET_ADDITIONALNEEDS));

        AuthHelper.getAuthToken();
        BookingHelper.patchBooking();
        BookingHelper.getBooking(200);

        assertEquals(GetVariable(PATCH_FIRSTNAME), GetVariable(GET_FIRSTNAME));

        BookingHelper.putBooking();
        BookingHelper.getBooking(200);

        assertEquals(GetVariable(PUT_FIRSTNAME), GetVariable(GET_FIRSTNAME));
        assertEquals(GetVariable(PUT_LASTNAME), GetVariable(GET_LASTNAME));
        assertEquals(GetVariable(PUT_TOTALPRICE), GetVariable(GET_TOTALPRICE));
        assertEquals(GetVariable(PUT_DEPOSITPAID), GetVariable(GET_DEPOSITPAID));
        assertEquals(GetVariable(PUT_CHECKIN), GetVariable(GET_CHECKIN));
        assertEquals(GetVariable(PUT_CHECKOUT), GetVariable(GET_CHECKOUT));
        assertEquals(GetVariable(PUT_ADDITIONALNEEDS), GetVariable(GET_ADDITIONALNEEDS));

        BookingHelper.deleteBooking();
        BookingHelper.getBooking(404);
    }
}
