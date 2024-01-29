package no.uio.ifi.tc.model;

import java.util.Arrays;
import lombok.Getter;

/** Type of the environment to work against. PRODUCTION is the default one. */
public enum Environment {
  PRODUCTION(""),
  INTERNAL("internal."),
  TESTING("test.");

  @Getter private String environment;

  Environment(String environment) {
    this.environment = environment;
  }

  public static Environment get(String environment) {
    return Arrays.stream(Environment.values())
        .filter(e -> e.name().equalsIgnoreCase(environment))
        .findAny()
        .orElse(PRODUCTION);
  }
}
