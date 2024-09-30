package no.elixir.fega.ltp.controllers.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.elixir.fega.ltp.dto.HeartbeatStatus;
import no.elixir.fega.ltp.services.HeartbeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartbeatController {

  private final HeartbeatService heartbeatService;

  public HeartbeatController(HeartbeatService heartbeatService) {
    this.heartbeatService = heartbeatService;
  }

  @GetMapping("/heartbeat")
  public ResponseEntity<HeartbeatStatus> getHeartbeatStatus() {
    try {
      return ResponseEntity.ok(heartbeatService.getHeartbeatStatus());
    } catch (JsonProcessingException e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
