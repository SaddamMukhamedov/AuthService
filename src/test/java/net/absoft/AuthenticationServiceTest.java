package net.absoft;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class AuthenticationServiceTest {
  private AuthenticationService authenticationService;
  @BeforeMethod
  public void setUp() {
    authenticationService = new AuthenticationService();
    System.out.println("SetUp");
  }

  @Test (
          groups = "positive"

  )
  public void testSample() throws InterruptedException {
    Thread.sleep(2000);
    System.out.println("testSample "+ new Date());
    fail("Failing test");
  }


  @Test (
          groups = "positive"
  )
  public void testSuccessfulAuthentication() {
    Response response =  authenticationService.authenticate("user1@test.com", "password1");
    assertEquals(response.getCode(), 200, "Response code should be 200");
    assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
  }


@DataProvider(name = "invalidLogins")
  public Object[][] invalidLogins() {
  return new Object[][]{
          new Object[]{"user1@test.com", "wrong_password1", new Response(401, "Invalid email or password")},
          new Object[]{"", "wrong_password1", new Response(400, "Email should not be empty string")},
          new Object[]{"user1@test.com", "", new Response(400, "Password should not be empty string")},
          new Object[]{"user1", "wrong_password1", new Response(400, "Invalid email")}
  };
}


  @Test (
          groups = "negative",
          dataProvider = "invalidLogins"
  )
  public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
    Response actualResponse = authenticationService
        .authenticate(email, password);
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 401");
    sa.assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(),
        "Response message should be \"Invalid email or password\"");
    sa.assertAll();
  }

//  @Test (groups = "negative")
//  public void testAuthenticationWithEmptyEmail() {
//    Response response = authenticationService.authenticate("", "password1");
//    assertEquals(response.getCode(), 400, "Response code should be 400");
//    assertEquals(response.getMessage(), "Email should not be empty string",
//        "Response message should be \"Email should not be empty string\"");
//  }

  @Test(groups ="negative")
  public void testAuthenticationWithInvalidEmail() {
    Response response = authenticationService.authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 200");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");
  }

  @Test(groups = "negative")
  public void testAuthenticationWithEmptyPassword() {
    Response response = authenticationService.authenticate("user1@test", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
