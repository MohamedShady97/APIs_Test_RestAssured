
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.util.Date;

public class EndToEndTest {

    String email ;
    String password ;
    String token;
    String name;
    String phone;
    String company;
    String newPassword;
    String noteID;
    String userID;
    
    @BeforeClass 
    
    public  void beforeClass(){
        RestAssured.baseURI = "https://practice.expandtesting.com/notes/api";
    }
    @Test
    public void testHealthCheck() {
        Response response = RestAssured.given()
                .when()
                .get("/health-check")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Response Body: " + response.getBody().asString());
    }

    @Test(priority = 0)
    public void testRegister() {
        name = "mohamed_shady";
        email = "\"mohamed_shady" + new Date().getTime() + "@gmail.com\"";
        password = "\"123456\"";

        String requestBody = "{\n" +
                "  \"name\": " + "\"" +name +"\"" +  ",\n" +
                "  \"email\": " + email + ",\n" +
                "  \"password\": " + password + "\n" +
                "}";

        System.out.println(requestBody);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/users/register");

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        userID = response.jsonPath().getString("data.id");

        if (response.getStatusCode() == 201) {
            System.out.println("User successfully registered.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    @Test(priority = 1)
    public void testLogin() {

        String requestBody = "{\n" + "  \"email\": " + email + ",\n" +
                "  \"password\": " + password + "\n" +
                "}";

        System.out.println(requestBody);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when().post("/users/login")
                .then().assertThat().statusCode(200).extract().response();


        String nameInResponse = response.jsonPath().get("data.name");
        Assert.assertEquals(nameInResponse, name);
        token = response.jsonPath().get("data.token");
        System.out.println(token);

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        if (response.getStatusCode() == 201) {
            System.out.println("User successfully registered.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    @Test(priority = 2)
    public void getProfileInfo() {

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("x-auth-token", token)
                .when().get("/users/profile")
                .then().assertThat().statusCode(200).extract().response();


        String nameInResponse = response.jsonPath().get("data.name");
        Assert.assertEquals(nameInResponse, name);

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }

    @Test(priority = 3)
    public void updateProfileInfo() {

        name = "mohamed_shady2";
        phone = "\"0103233322\"";
        company = "\"123456\"";

        String requestBody = "{\n" +
                "  \"name\": " + "\"" +name +"\"" +  ",\n" +
                "  \"phone\": " + phone + ",\n" +
                "  \"company\": " + company + "\n" +
                "}";

        System.out.println(requestBody);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("x-auth-token", token)
                .body(requestBody)
                .when()
                .patch("/users/profile");

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        String nameInResponse = response.jsonPath().get("data.name");
        Assert.assertEquals(nameInResponse, name);

        if (response.getStatusCode() == 200) {
            System.out.println("User successfully Updated.");
        } else {
            System.out.println("Update failed.");
        }
    }

    @Test(priority = 4)
    public void testChangePassword() {

        newPassword = "\"0103233322\"";

        String requestBody = "{\n" +
                "  \"currentPassword\": "  + password +  ",\n" +
                "  \"newPassword\": " + newPassword + "\n" +
                "}";

        System.out.println(requestBody);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("x-auth-token", token)
                .body(requestBody)
                .when()
                .post("/users/change-password");

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        String messageResponse = response.jsonPath().get("message");
        Assert.assertEquals(messageResponse, "The password was successfully updated");

        if (response.getStatusCode() == 200) {
            System.out.println("Password successfully Updated.");
        } else {
            System.out.println("Password updated failed.");
        }
    }

    @Test(priority = 5)
    public void CreateNote() {
        String title = "My First Note";
        String description = "\"My First Note Description\"";;
        String category = "\"Home\"";;

        String requestBody = "{\n" +
                "  \"title\": " + "\"" +title +"\"" +  ",\n" +
                "  \"description\": " + description + ",\n" +
                "  \"category\": " + category + "\n" +
                "}";

        System.out.println(requestBody);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("x-auth-token", token)
                .body(requestBody)
                .post("/notes");

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Note creation failed!");

        String titleInResponse = response.jsonPath().getString("data.title");
        Assert.assertEquals(titleInResponse, "My First Note", "Title does not match!");
        noteID = response.jsonPath().get("data.id");
        String descriptionInResponse = response.jsonPath().getString("data.description");
        Assert.assertEquals(descriptionInResponse, "My First Note Description", "Description does not match!");

        String categoryInResponse = response.jsonPath().getString("data.category");
        Assert.assertEquals(categoryInResponse, "Home", "Category does not match!");
    }


    @Test(priority = 6)
    public void updateNote() {

        String updateNoteBody = "{\n" +
                "  \"id\": " + "\"" +userID +"\"" +  ",\n" +
                "  \"title\": \"Updated Title\",\n" +
                "  \"description\": \"Updated Description\",\n" +
                "  \"completed\": false, \n" +
                "  \"category\": \"Work\"\n" +
                "}";

        System.out.println(updateNoteBody);

        Response updateResponse = RestAssured.given()
                .header("x-auth-token", token)
                .header("Content-Type", "application/json")
                .body(updateNoteBody)
                .put("/notes/" + noteID );

        System.out.println("Response Status Code: " + updateResponse.getStatusCode());
        System.out.println("Response Body: " + updateResponse.getBody().asString());


        Assert.assertEquals(updateResponse.getStatusCode(), 200, "Note update failed!");

        String updatedTitle = updateResponse.jsonPath().getString("data.title");
        String updatedid= updateResponse.jsonPath().getString("data.id");
        String updatedDescription = updateResponse.jsonPath().getString("data.description");
        String updatedCategory = updateResponse.jsonPath().getString("data.category");

        Assert.assertEquals(updatedid, noteID, "Id not updated!");
        Assert.assertEquals(updatedTitle, "Updated Title", "Title not updated!");
        Assert.assertEquals(updatedDescription, "Updated Description", "Description not updated!");
        Assert.assertEquals(updatedCategory, "Work", "Category not updated!");
    }
    @Test(priority =7 )
    public void deleteNote() {

        Response deleteResponse = RestAssured.given()
                .header("x-auth-token", token)
                .delete("/notes/" + noteID);

        System.out.println("Response Status Code: " + deleteResponse.getStatusCode());
        System.out.println("Response Body: " + deleteResponse.getBody().asString());

        Assert.assertEquals(deleteResponse.getStatusCode(), 200, "Note deletion failed!");

        String message = deleteResponse.jsonPath().getString("message");
        Assert.assertEquals(message, "Note successfully deleted", "Unexpected response message!");

        Response getResponse = RestAssured.given()
                .header("x-auth-token", token)
                .get("/notes/" + noteID);

        Assert.assertEquals(getResponse.getStatusCode(), 404, "Note still exists after deletion!");
    }
}
