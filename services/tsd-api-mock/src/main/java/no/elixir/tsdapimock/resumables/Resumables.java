package no.elixir.tsdapimock.resumables;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Resumables {

  private final ResumablesRepository resumablesRepository;

  @Autowired
  public Resumables(ResumablesRepository resumablesRepository) {
    this.resumablesRepository = resumablesRepository;
  }

  public static ResumableUploadDto convertToDto(ResumableUpload entity) {
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

  public ArrayList<ResumableUpload> readResumableChunks() {
    return new ArrayList<>((Collection<? extends ResumableUpload>) resumablesRepository.findAll());
  }
}
