package no.elixir.fega.ltp.authentication;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;
import no.elixir.fega.ltp.dto.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Component that queries CEGA for user credentials. */
@Component
public class CEGACredentialsProvider {

  private final RestTemplate restTemplate;

  @Value("${cega.auth-url}")
  private String cegaAuthURL;

  @Value("${cega.username}")
  private String cegaUsername;

  @Value("${cega.password}")
  private String cegaPassword;

  @Autowired
  public CEGACredentialsProvider(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Gets CEGA credentials from CEGA auth endpoint, the method is cached.
   *
   * @param username CEGA username.
   * @return <code>Credentials</code> POJO.
   * @throws MalformedURLException In case CEGA auth endpoint URL is malformed.
   * @throws URISyntaxException In case CEGA auth endpoint URL is malformed.
   */
  @Cacheable("cega-credentials")
  public Credentials getCredentials(String username)
      throws MalformedURLException, URISyntaxException {
    URI uri = new URI(String.format(cegaAuthURL + "%s?idType=username", username));
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.set(
        HttpHeaders.AUTHORIZATION,
        "Basic "
            + Base64.getEncoder().encodeToString((cegaUsername + ":" + cegaPassword).getBytes()));
    ResponseEntity<Credentials> response =
        restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), Credentials.class);
    return Objects.requireNonNull(response.getBody());
  }
}
