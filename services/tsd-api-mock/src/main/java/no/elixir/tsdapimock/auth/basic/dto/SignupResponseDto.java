package no.elixir.tsdapimock.auth.basic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignupResponseDto(@JsonProperty("client_id") String clientId) {}
