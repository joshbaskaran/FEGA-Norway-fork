package no.elixir.tsdapimock.files.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteResumableDto(@JsonProperty("message") String message) {}
