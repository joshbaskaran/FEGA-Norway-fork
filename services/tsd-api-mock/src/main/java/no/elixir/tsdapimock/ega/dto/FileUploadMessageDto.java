package no.elixir.tsdapimock.ega.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileUploadMessageDto(@JsonProperty("message") String message) {}
