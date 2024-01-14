package no.elixir.tsdapimock.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private String SECRET_KEY;

  @Value("${tsd.file.secretkey}")
  public void setSecretKey(String secretKey) {
    this.SECRET_KEY = secretKey;
  }

  public String createJwt(
      String project, String id, String issuer, String subject, long ttlMillis) {
    Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    var tokenBuilder =
        Jwts.builder()
            .id(id)
            .issuer(issuer)
            .subject(subject)
            .claim("user", project + "-" + subject)
            .signWith(key);

    var nowMillis = System.currentTimeMillis();
    var now = new Date(nowMillis);

    if (ttlMillis > 0) {
      var expMillis = nowMillis + ttlMillis;
      var exp = new Date(expMillis);
      tokenBuilder.expiration(exp);
    }

    return tokenBuilder.compact();
  }

  public boolean verify(String authorizationHeader) {
    return true;
  }
}
