package no.elixir.tsdapimock.ega.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FolderMessageDto(@JsonProperty("message") String message) {}
