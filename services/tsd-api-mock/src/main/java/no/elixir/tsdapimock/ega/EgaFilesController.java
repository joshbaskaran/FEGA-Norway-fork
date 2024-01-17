package no.elixir.tsdapimock.ega;

import no.elixir.tsdapimock.exceptions.CredentialsMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/{project}/ega/{userName}")
public class EgaFilesController {
  private final EgaFilesService egaFilesService;

  @Autowired
  public EgaFilesController(EgaFilesService egaFilesService) {
    this.egaFilesService = egaFilesService;
  }

  @GetMapping(value = "/resumables", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getResumableUploads(
      @PathVariable String project,
      @PathVariable String userName,
      @RequestHeader("Authorization") String authorization) {
    try {
      var response = egaFilesService.getResumableUploads(project, userName, authorization);
      return ResponseEntity.ok(response);
    } catch (CredentialsMismatchException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
