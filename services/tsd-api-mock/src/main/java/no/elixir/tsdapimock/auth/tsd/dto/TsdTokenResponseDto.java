package no.elixir.tsdapimock.auth.tsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TsdTokenResponseDto(@JsonProperty("token") String token) {}
