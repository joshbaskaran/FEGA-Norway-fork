package no.elixir.tsdapimock.resumables;

import java.util.ArrayList;
import lombok.Data;

@Data
public class ResumableUploadsResponseDto {
  private ArrayList<ResumableUploadDto> resumables;

  public ResumableUploadsResponseDto(ArrayList<ResumableUploadDto> resumableDtos) {
    this.resumables = new ArrayList<>(resumableDtos);
  }
}
