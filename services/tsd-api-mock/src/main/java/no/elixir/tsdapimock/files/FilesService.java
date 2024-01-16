package no.elixir.tsdapimock.files;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import no.elixir.tsdapimock.exceptions.CredentialsMismatchException;
import no.elixir.tsdapimock.exceptions.FailedResourceCreationException;
import no.elixir.tsdapimock.files.dto.FileUploadMessageDto;
import no.elixir.tsdapimock.files.dto.FolderMessageDto;
import no.elixir.tsdapimock.files.dto.ResumableUploadDto;
import no.elixir.tsdapimock.files.dto.ResumableUploadsResponseDto;
import no.elixir.tsdapimock.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FilesService {
  private final JwtService jwtService;
  private final ResumablesRepository resumablesRepository;

  @Value("${tsd.file.import}")
  public String durableFileImport;

  @Autowired
  public FilesService(ResumablesRepository resumablesRepository, JwtService jwtService) {
    this.resumablesRepository = resumablesRepository;
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

  public FolderMessageDto createFolder(
      String project, String authorizationHeader, String folderName) {
    if (!jwtService.verify(authorizationHeader)) {
      throw new CredentialsMismatchException("Invalid bearer authorization token");
    }
    var path = Paths.get(String.format(durableFileImport, project), folderName);
    try {
      Files.createDirectories(path);
      log.info("created: " + path);
    } catch (IOException e) {
      throw new FailedResourceCreationException(e.getClass().getTypeName() + e.getMessage());
    }
    return new FolderMessageDto("folder created");
  }

  public ResumableUploadsResponseDto getResumableUploads(
      String project, String authorizationHeader) {
    if (!jwtService.verify(authorizationHeader)) {
      throw new CredentialsMismatchException("Invalid Authorization Header");
    }
    var resumableChunks = readResumableChunks();
    ArrayList<ResumableUploadDto> dtoList =
        resumableChunks.stream()
            .map(this::convertToDto)
            .collect(Collectors.toCollection(ArrayList::new));

    return new ResumableUploadsResponseDto(dtoList);
  }

  private ResumableUploadDto convertToDto(ResumableUpload entity) {
    return new ResumableUploadDto(
        entity.getId(),
        entity.getFileName(),
        entity.getMemberGroup(),
        entity.getChunkSize(),
        entity.getPreviousOffset(),
        entity.getNextOffset(),
        entity.getMaxChunk(),
        entity.getMd5Sum());
  }

  private ArrayList<ResumableUpload> readResumableChunks() {
    return new ArrayList<>((Collection<? extends ResumableUpload>) resumablesRepository.findAll());
  }
}
