package no.elixir.tsdapimock;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.elixir.tsdapimock.auth.basic.Client;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebLayerTest {

  private static Client client;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeAll
  public static void setUp() {
    client = Client.builder().name("test").email("test@example.com").build();
  }

  @Nested
  class BasicAuth {
    private final String basicAuthUrl = "/v1/p-test/auth/basic";

    @Test
    public void testSignup() throws Exception {
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

    @Nested
    class BasicAuthSignupConfirmation {
      @Test
      public void testSignupConfirmation() throws Exception {
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
            restTemplate.postForEntity(
                basicAuthUrl + "/signupconfirm", requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        var responseJson = new ObjectMapper().readTree(response.getBody());
        var confirmationToken = responseJson.get("confirmation_token").asText();
        assertThat(confirmationToken).isNotBlank();

        client.setConfirmationToken(confirmationToken);
      }

      @Nested
      class BasicAuthConfirmation {
        @Test
        public void testConfirmation() throws Exception {
          var requestBody =
              new ObjectMapper()
                  .createObjectNode()
                  .put("client_id", client.getId())
                  .put("confirmation_token", client.getConfirmationToken())
                  .toString();

          var requestHeaders = new HttpHeaders();
          requestHeaders.setContentType(MediaType.APPLICATION_JSON);

          var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

          ResponseEntity<String> response =
              restTemplate.postForEntity(basicAuthUrl + "/confirm", requestEntity, String.class);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isNotNull();

          var responseJson = new ObjectMapper().readTree(response.getBody());
          var password = responseJson.get("password").asText();
          assertThat(password).isNotBlank();

          client.setPassword(password);
        }

        @Nested
        class BasicAuthApiKey {
          @Test
          public void testApiKey() throws Exception {
            var requestBody =
                new ObjectMapper()
                    .createObjectNode()
                    .put("client_id", client.getId())
                    .put("password", client.getPassword())
                    .toString();

            var requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

            ResponseEntity<String> response =
                restTemplate.postForEntity(basicAuthUrl + "/api_key", requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var responseJson = new ObjectMapper().readTree(response.getBody());
            var apiKey = responseJson.get("api_key").asText();
            assertThat(apiKey).isNotBlank();
          }
        }
      }
    }
  }
}
