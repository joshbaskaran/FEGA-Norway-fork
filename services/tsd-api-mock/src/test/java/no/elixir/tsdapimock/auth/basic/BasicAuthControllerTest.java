package no.elixir.tsdapimock.auth.basic;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicAuthControllerTest {

  private final String basicAuthUrl = "/v1/p-test/auth/basic/";
  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void testSignup() throws Exception {
    var requestBody =
        new ObjectMapper()
            .createObjectNode()
            .put("client_name", "test")
            .put("EMAIL", "test@example.com")
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
  }
}
