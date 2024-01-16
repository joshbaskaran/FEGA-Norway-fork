package no.elixir.tsdapimock.exceptions;

public class FailedResourceCreationException extends RuntimeException {
  public FailedResourceCreationException(String message) {
    super(message);
  }
}
