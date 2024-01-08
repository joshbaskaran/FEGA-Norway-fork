package no.elixir.tsdapimock.auth.basic;

import static javax.management.timer.Timer.ONE_HOUR;

import no.elixir.tsdapimock.auth.basic.dto.SignupConfirmRequestDto;
import no.elixir.tsdapimock.auth.basic.dto.SignupConfirmResponseDto;
import no.elixir.tsdapimock.auth.basic.dto.SignupRequestDto;
import no.elixir.tsdapimock.auth.basic.dto.SignupResponseDto;
import no.elixir.tsdapimock.exceptions.CredentialsMismatchException;
import no.elixir.tsdapimock.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BasicAuthService {

  private final ClientRepository clientRepository;
  private final JwtService jwtService;

  @Autowired
  public BasicAuthService(ClientRepository clientRepository, JwtService jwtService) {
    this.clientRepository = clientRepository;
    this.jwtService = jwtService;
  }

  public SignupResponseDto signup(String project, SignupRequestDto request) {
    var confirmationToken =
        jwtService.createJwt(project, request.email(), "TSD", request.email(), ONE_HOUR);
    var client =
        Client.builder()
            .name(request.clientName())
            .email(request.email())
            .userName(request.email())
            .confirmationToken(confirmationToken)
            .build();

    var savedClient = clientRepository.save(client);

    return new SignupResponseDto(savedClient.getId());
  }

  public SignupConfirmResponseDto signupConfirm(String project, SignupConfirmRequestDto request) {
    var client = clientRepository.findById(request.clientId()).orElseThrow();

    if (!client.getEmail().equals(request.email())) {
      throw new CredentialsMismatchException("Incorrect email ID provided");
    }

    if (!client.getName().equals(request.clientName())) {
      throw new CredentialsMismatchException("Incorrect client name");
    }

    return new SignupConfirmResponseDto(client.getConfirmationToken());
  }
}
