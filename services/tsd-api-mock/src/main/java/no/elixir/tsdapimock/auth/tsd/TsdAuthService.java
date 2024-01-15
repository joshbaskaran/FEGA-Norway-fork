package no.elixir.tsdapimock.auth.tsd;

import static javax.management.timer.Timer.ONE_HOUR;

import no.elixir.tsdapimock.auth.tsd.dto.TsdTokenRequestDto;
import no.elixir.tsdapimock.auth.tsd.dto.TsdTokenResponseDto;
import no.elixir.tsdapimock.exceptions.CredentialsMismatchException;
import no.elixir.tsdapimock.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TsdAuthService {

  private final JwtService jwtService;

  @Autowired
  public TsdAuthService(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  public TsdTokenResponseDto getToken(
      String project, String authorizationHeader, TsdTokenRequestDto request) {
    if (!authorizationHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Header must have a BEARER token");
    }
    if (!jwtService.verify(authorizationHeader)) {
      throw new CredentialsMismatchException("Invalid BEARER Token");
    }
    var token =
        jwtService.createJwt(project, request.userName(), "TSD", request.userName(), ONE_HOUR);
    return new TsdTokenResponseDto(token);
  }
}
