package no.elixir.tsdapimock.auth.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/{project}/auth/basic")
public class BasicAuthController {

  private final BasicAuthService basicAuthService;

  @Autowired
  public BasicAuthController(BasicAuthService basicAuthService) {
    this.basicAuthService = basicAuthService;
  }
}
