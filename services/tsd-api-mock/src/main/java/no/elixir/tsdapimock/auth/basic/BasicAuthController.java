package no.elixir.tsdapimock.auth.basic;

import jakarta.validation.Valid;
import no.elixir.tsdapimock.auth.basic.dto.SignupRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/{project}/auth/basic")
public class BasicAuthController {

  private final BasicAuthService basicAuthService;

  @Autowired
  public BasicAuthController(BasicAuthService basicAuthService) {
    this.basicAuthService = basicAuthService;
  }

  @PostMapping(
      value = "/signup",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> singup(
      @PathVariable String project, @Valid @RequestBody SignupRequestDto request) {
    var response = basicAuthService.signup(project, request);
    return ResponseEntity.ok(response);
  }
}
