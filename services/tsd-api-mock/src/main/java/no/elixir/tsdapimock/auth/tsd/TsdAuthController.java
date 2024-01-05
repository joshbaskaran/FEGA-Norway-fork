package no.elixir.tsdapimock.auth.tsd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/{project}/auth/tsd")
public class TsdAuthController {
  private final TsdAuthService tsdAuthService;

  @Autowired
  public TsdAuthController(TsdAuthService tsdAuthService) {
    this.tsdAuthService = tsdAuthService;
  }
}
