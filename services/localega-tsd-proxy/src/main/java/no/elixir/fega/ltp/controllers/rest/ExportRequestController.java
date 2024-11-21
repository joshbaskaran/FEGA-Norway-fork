package no.elixir.fega.ltp.controllers.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import no.elixir.fega.ltp.dto.ExportRequest;
import no.elixir.fega.ltp.dto.GenericResponse;
import no.elixir.fega.ltp.services.ExportRequestService;
import no.elixir.fega.ltp.services.TokenService;
import no.uio.ifi.clearinghouse.model.Visa;
import no.uio.ifi.clearinghouse.model.VisaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ExportRequestController {

    private final TokenService tokenService;
    private final ExportRequestService exportRequestService;

    @Autowired
    public ExportRequestController(TokenService tokenService, ExportRequestService exportRequestService) {
        this.tokenService = tokenService;
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
            String subject = tokenService.getSubject(accessToken);
            List<Visa> controlledAccessGrantsVisas =
                    tokenService.filterByVisaType(
                            tokenService.fetchTheFullPassportUsingPassportScopedAccessTokenAndGetVisas(
                                    accessToken),
                            VisaType.ControlledAccessGrants);
            log.info(
                    "Elixir user {} authenticated and provided following valid GA4GH Visas: {}",
                    subject,
                    controlledAccessGrantsVisas);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericResponse(e.getMessage()));
        }

        exportRequestService.exportRequest(body);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponse("Export request completed successfully"));

    }

}
