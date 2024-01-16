package no.elixir.tsdapimock.files;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import no.elixir.tsdapimock.exceptions.CredentialsMismatchException;
import no.elixir.tsdapimock.files.dto.FileUploadMessageDto;
import no.elixir.tsdapimock.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FilesService {
  private final JwtService jwtService;

  @Value("${tsd.file.import}")
  public String durableFileImport;

  @Autowired
  public FilesService(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  public FileUploadMessageDto upload(
      String project, String authorizationHeader, String fileName, InputStream fileStream) {
    if (!authorizationHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Header must contain a bearer auth token");
    }
    if (!jwtService.verify(authorizationHeader)) {
      throw new CredentialsMismatchException("Stream processing failed");
    }

    var path = Paths.get(String.format(durableFileImport, project), fileName);
    try {
      Files.copy(fileStream, path, StandardCopyOption.REPLACE_EXISTING);
      log.info(path.getParent().toString());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return new FileUploadMessageDto("Data Streamed");
  }
}
