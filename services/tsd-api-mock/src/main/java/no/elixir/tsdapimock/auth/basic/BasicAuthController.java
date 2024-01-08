package no.elixir.tsdapimock.auth.basic;

import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import no.elixir.tsdapimock.auth.basic.dto.SignupConfirmRequestDto;
import no.elixir.tsdapimock.auth.basic.dto.SignupRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<?> signup(
      @PathVariable String project, @Valid @RequestBody SignupRequestDto request) {
    var response = basicAuthService.signup(project, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping(
      value = "/signupconfirm",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> signupConfirm(
      @PathVariable String project, @Valid @RequestBody SignupConfirmRequestDto request) {
    try {
      var response = basicAuthService.signupConfirm(project, request);
      return ResponseEntity.ok(response);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
