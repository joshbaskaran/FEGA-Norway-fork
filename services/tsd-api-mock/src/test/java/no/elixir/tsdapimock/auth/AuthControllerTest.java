package no.elixir.tsdapimock.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.elixir.tsdapimock.auth.basic.Client;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

  private static Client client;
  private final String basicAuthUrl = "/v1/p-test/auth/basic/";
  @Autowired private TestRestTemplate restTemplate;

  @BeforeAll
  public static void setup() {
    client = Client.builder().name("test").email("test@example.com").build();
  }

  @Test
  @Order(1)
  public void testBasicAuthSignup() throws Exception {
    var requestBody =
        new ObjectMapper()
            .createObjectNode()
            .put("client_name", client.getName())
            .put("EMAIL", client.getEmail())
            .toString();

    var requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

    var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

    ResponseEntity<String> response =
        restTemplate.postForEntity(basicAuthUrl + "/signup", requestEntity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var responseJson = new ObjectMapper().readTree(response.getBody());

    var clientId = responseJson.get("client_id");
    assertThat(clientId).isNotNull();
    assertThat(clientId.textValue()).isNotBlank();

    client.setId(clientId.textValue());
  }

  @Test
  @Order(2)
  public void testBasicAuthSignupConfirmation() throws Exception {
    var requestBody =
        new ObjectMapper()
            .createObjectNode()
            .put("client_id", client.getId())
            .put("EMAIL", client.getEmail())
            .put("client_name", client.getName())
            .toString();

    var requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

    var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

    ResponseEntity<String> response =
        restTemplate.postForEntity(basicAuthUrl + "/signupconfirm", requestEntity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var responseJson = new ObjectMapper().readTree(response.getBody());
    var confirmationToken = responseJson.get("confirmation_token").asText();
    assertThat(confirmationToken).isNotNull();
    assertThat(confirmationToken).isNotBlank();

    client.setConfirmationToken(confirmationToken);
  }
}
