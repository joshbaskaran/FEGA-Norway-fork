package no.elixir.tsdapimock.exceptions;

public class FailedResourceDeletionException extends RuntimeException {
  public FailedResourceDeletionException(String message) {
    super(message);
  }
}
