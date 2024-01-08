package no.elixir.tsdapimock.auth.basic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfirmResponseDto(@JsonProperty("password") String password) {}
