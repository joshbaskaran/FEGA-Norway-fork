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

  /**
   * Converts a ResumableUpload entity to a ResumableUploadDto object.
   *
   * @param entity The ResumableUpload entity to convert.
   * @return A ResumableUploadDto object representing the converted entity.
   */
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

  /**
   * Reads all the resumable chunks from the repository.
   *
   * @return An ArrayList of ResumableUpload objects representing the resumable chunks.
   */
  public ArrayList<ResumableUpload> readResumableChunks() {
    return new ArrayList<>((Collection<? extends ResumableUpload>) resumablesRepository.findAll());
  }
}
