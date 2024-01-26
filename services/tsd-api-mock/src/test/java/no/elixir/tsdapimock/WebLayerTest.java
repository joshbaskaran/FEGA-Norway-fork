package no.elixir.tsdapimock;

import static javax.management.timer.Timer.ONE_HOUR;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import no.elixir.tsdapimock.auth.basic.Client;
import no.elixir.tsdapimock.utils.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
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

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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

      @Test
      public void testGetBasicToken() throws Exception {
        var requestBody = new ObjectMapper().createObjectNode().put("type", "string").toString();

        var requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth("123abc");
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity<String> response =
            restTemplate.postForEntity(basicAuthUrl + "/token", requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        var responseJson = new ObjectMapper().readTree(response.getBody());
        var token = responseJson.get("token").asText();
        assertThat(token).isNotBlank();
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

  @Nested
  class TsdAuth {
    private final String tsdAuthUrl = "/v1/p-test/auth/tsd";

    @Test
    public void testGetTsdToken() throws Exception {
      var requestBody =
          new ObjectMapper()
              .createObjectNode()
              .put("user_name", "test")
              .put("otp", "1234")
              .put("password", "password")
              .toString();

      var requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      requestHeaders.setBearerAuth("abc123");

      var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

      ResponseEntity<String> response =
          restTemplate.postForEntity(tsdAuthUrl + "/token", requestEntity, String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();

      var responseJson = new ObjectMapper().readTree(response.getBody());
      var token = responseJson.get("token");
      assertThat(token).isNotNull();
      assertThat(token.textValue()).isNotBlank();
    }
  }

  @Nested
  class ElixirAuth {

    private final String elixirAuthUrl = "/v1/p-test/auth/elixir";
    private final JwtService jwtService;

    @Autowired
    ElixirAuth(JwtService jwtService) {
      this.jwtService = jwtService;
    }

    @Test
    public void testElixirToken() throws Exception {
      var jwtToken =
          jwtService.createJwt("p-test", client.getEmail(), "TSD", client.getEmail(), ONE_HOUR);
      var requestBody = new ObjectMapper().createObjectNode().put("idtoken", jwtToken).toString();

      var requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      requestHeaders.setBearerAuth("abc123");

      var requestEntity = new HttpEntity<>(requestBody, requestHeaders);

      ResponseEntity<String> response =
          restTemplate.postForEntity(elixirAuthUrl + "/token", requestEntity, String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();

      var responseJson = new ObjectMapper().readTree(response.getBody());
      var token = responseJson.get("token");
      assertThat(token).isNotNull();
      assertThat(token.textValue()).isNotBlank();
    }
  }

  @Nested
  class FileHandling {
    private final String filesUrl = "/v1/p-test/files";

    @Test
    public void testFilesUpload() throws Exception {
      byte[] fileContent = "test content".getBytes();

      var authHeader = "Bearer validToken";
      var fileName = "testFile.txt";

      var requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      requestHeaders.set("Authorization", authHeader);
      requestHeaders.set("filename", fileName);

      var requestEntity = new HttpEntity<>(fileContent, requestHeaders);

      ResponseEntity<String> response =
          restTemplate.exchange(filesUrl + "/stream", HttpMethod.PUT, requestEntity, String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(response.getBody()).isNotBlank();

      var responseJson = new ObjectMapper().readTree(response.getBody());
      var message = responseJson.get("message");
      assertThat(message.textValue()).isNotBlank();
    }

    @Test
    public void testFilesCreateFolder() throws Exception {
      var authHeader = "Bearer validToken";
      var folderName = "testFolder";

      var requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      requestHeaders.set("Authorization", authHeader);

      var requestEntity = new HttpEntity<>(null, requestHeaders);

      ResponseEntity<String> response =
          restTemplate.exchange(
              filesUrl
                  + "/folder"
                  + "?name="
                  + URLEncoder.encode(folderName, StandardCharsets.UTF_8),
              HttpMethod.PUT,
              requestEntity,
              String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(response.getBody()).isNotBlank();

      var responseJson = new ObjectMapper().readTree(response.getBody());
      var message = responseJson.get("message");
      assertThat(message.textValue()).isNotBlank();
    }

    @Test
    public void testGetResumableUploads() throws Exception {
      var authHeader = "Bearer validToken";

      var requestHeaders = new HttpHeaders();
      requestHeaders.set("Authorization", authHeader);

      var requestEntity = new HttpEntity<>(requestHeaders);

      ResponseEntity<String> response =
          restTemplate.exchange(
              filesUrl + "/resumables", HttpMethod.GET, requestEntity, String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotBlank();

      var responseJson = new ObjectMapper().readTree(response.getBody());
      var resumables = responseJson.get("resumables");
      assertThat(resumables).isNotNull();
      assertThat(resumables.isArray()).isTrue();
    }

    //     TODO: Fix this test

    @Test
    public void testHandleResumableUpload() throws Exception {
      byte[] firstChunkContent = "First chunk content".getBytes();
      byte[] secondChunkContent = "Second chunk content".getBytes();
      byte[] finalChunkContent = "Final chunk content".getBytes();

      var authHeader = "Bearer validToken";
      var fileName = "resumableFile.txt";

      uploadChunk(fileName, firstChunkContent, authHeader, "1", null);

      var uploadId = "someGeneratedUploadId";
      uploadChunk(fileName, secondChunkContent, authHeader, "2", uploadId);

      ResponseEntity<String> finalResponse =
          uploadChunk(fileName, finalChunkContent, authHeader, "end", uploadId);

      assertThat(finalResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(finalResponse.getBody()).isNotBlank();

      var responseJson = new ObjectMapper().readTree(finalResponse.getBody());
      var message = responseJson.get("message");
      assertThat(message.textValue()).isNotBlank();
    }

    private ResponseEntity<String> uploadChunk(
        String fileName, byte[] content, String authHeader, String chunk, String uploadId) {
      var requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      //      requestHeaders.set("X-HTTP-Method-override", "PATCH");
      requestHeaders.set("Authorization", authHeader);
      requestHeaders.set("filename", fileName);
      if (uploadId != null) {
        requestHeaders.set("id", uploadId);
      }

      var requestEntity = new HttpEntity<>(content, requestHeaders);
      return restTemplate.exchange(
          filesUrl + "/stream/" + fileName + "?chunk=" + chunk,
          HttpMethod.PATCH,
          requestEntity,
          String.class);
    }
  }

  @Nested
  class Ega {

    private final String egaUrl = "/v1/p-test/ega/testUser";

    @Test
    public void testGetResumableUploads() throws Exception {
      var authHeader = "Bearer validToken";

      var requestHeaders = new HttpHeaders();
      requestHeaders.set("Authorization", authHeader);

      var requestEntity = new HttpEntity<>(requestHeaders);

      ResponseEntity<String> response =
          restTemplate.exchange(
              egaUrl + "/resumables", HttpMethod.GET, requestEntity, String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotBlank();

      var responseJson = new ObjectMapper().readTree(response.getBody());
      var resumables = responseJson.get("resumables");
      assertThat(resumables).isNotNull();
      assertThat(resumables.isArray()).isTrue();
    }
  }
}
