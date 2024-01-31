package no.elixir.tsdapimock.auth.elixir.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ElixirTokenRequestDto(
    @JsonProperty("idtoken") @NotBlank(message = "ID token can not be blank") String token) {}
