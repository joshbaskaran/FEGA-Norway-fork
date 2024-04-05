package no.elixir.tsdapimock.auth.basic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignupConfirmResponseDto(
    @JsonProperty("confirmation_token") String confirmationToken) {}
