package no.elixir.tsdapimock.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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


  /**
   * Creates a JSON Web Token (JWT) with the given parameters.
   *
   * @param project the name of the project
   * @param id the ID of the token
   * @param issuer the issuer of the token
   * @param subject the subject of the token
   * @param ttlMillis the time to live (TTL) in milliseconds
   * @return the created JWT
   */
  public String createJwt(
      String project, String id, String issuer, String subject, long ttlMillis) {
    Key key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_KEY));

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

  /**
   * Retrieves the subject from a JSON Web Token (JWT).
   *
   * @param token the JWT token
   * @return the subject of the token
   * @throws JwtException if an error occurs while parsing or verifying the token
   */
  public String getSubject(String token) {
    var secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_KEY));
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject();
    } catch (JwtException e) {
      throw new JwtException(e.getMessage());
    }
  }
}
