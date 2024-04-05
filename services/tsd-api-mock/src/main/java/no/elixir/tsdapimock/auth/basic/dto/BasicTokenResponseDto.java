package no.elixir.tsdapimock.auth.basic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BasicTokenResponseDto(@JsonProperty("token") String token) {}
