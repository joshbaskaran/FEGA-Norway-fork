package no.elixir.tsdapimock.auth.elixir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/{project}/auth/elixir")
public class ElixirAuthController {
  private final ElixirAuthService elixirAuthService;

  @Autowired
  public ElixirAuthController(ElixirAuthService elixirAuthService) {
    this.elixirAuthService = elixirAuthService;
  }
}
