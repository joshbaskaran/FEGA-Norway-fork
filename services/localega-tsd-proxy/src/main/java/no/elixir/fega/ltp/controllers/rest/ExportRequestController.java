package no.elixir.fega.ltp.controllers.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import no.elixir.fega.ltp.dto.ExportRequest;
import no.elixir.fega.ltp.dto.GenericResponse;
import no.elixir.fega.ltp.services.ExportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ExportRequestController {

  private final ExportRequestService exportRequestService;

  @Autowired
  public ExportRequestController(ExportRequestService exportRequestService) {
    this.exportRequestService = exportRequestService;
  }

  @PostMapping("/export")
  public ResponseEntity<GenericResponse> exportRequest(
      HttpServletRequest request, @RequestBody @NotNull ExportRequest body) {
    String bearerAuth = request.getHeader(HttpHeaders.PROXY_AUTHORIZATION);
    if (bearerAuth == null || bearerAuth.isEmpty()) {
      log.info("Authentication attempt without Elixir AAI access token provided");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String accessToken = bearerAuth.replace("Bearer ", "");
    try {
      exportRequestService.exportRequest(accessToken, body);
    } catch (Exception e) {
      log.info(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse(e.getMessage()));
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(new GenericResponse("Export request completed successfully"));
  }
}
