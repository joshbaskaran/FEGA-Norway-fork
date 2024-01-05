package no.elixir.tsdapimock.auth.basic;

import no.elixir.tsdapimock.auth.basic.dto.SignupRequestDto;
import no.elixir.tsdapimock.auth.basic.dto.SignupResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BasicAuthService {

  private final ClientRepository clientRepository;

  @Autowired
  public BasicAuthService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public SignupResponseDto signup(String project, SignupRequestDto request) {
    var client =
        Client.builder()
            .name(request.clientName())
            .email(request.email())
            .userName(request.email())
            .build();

    var savedClient = clientRepository.save(client);

    return new SignupResponseDto(savedClient.getId());
  }
}
