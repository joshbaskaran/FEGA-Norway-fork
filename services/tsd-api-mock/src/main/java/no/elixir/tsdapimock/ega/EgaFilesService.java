package no.elixir.tsdapimock.ega;

import no.elixir.tsdapimock.exceptions.CredentialsMismatchException;
import no.elixir.tsdapimock.resumables.ResumableUploadDto;
import no.elixir.tsdapimock.resumables.ResumableUploadsResponseDto;
import no.elixir.tsdapimock.resumables.Resumables;
import no.elixir.tsdapimock.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class EgaFilesService {

  private final JwtService jwtService;
  private final Resumables resumables;

  @Autowired
  public EgaFilesService(JwtService jwtService, Resumables resumables) {
    this.jwtService = jwtService;
    this.resumables = resumables;
  }

  public ResumableUploadsResponseDto getResumableUploads(
      String project, String userName, String authorization) {
    if (!authorization.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Header must contains a Bearer token");
    }
    if (!jwtService.verify(authorization)) {
      throw new CredentialsMismatchException("Invalid authorization token");
    }
    var resumableChunks = resumables.readResumableChunks();
    ArrayList<ResumableUploadDto> dtoList =
        resumableChunks.stream()
            .map(Resumables::convertToDto)
            .collect(Collectors.toCollection(ArrayList::new));

    return new ResumableUploadsResponseDto(dtoList);
  }
}
