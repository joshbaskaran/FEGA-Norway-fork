package no.elixir.tsdapimock.ega;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/{project}/ega/{userName}/files")
public class EgaFilesController {
  private final EgaFilesService egaFilesService;

  @Autowired
  public EgaFilesController(EgaFilesService egaFilesService) {
    this.egaFilesService = egaFilesService;
  }
}
