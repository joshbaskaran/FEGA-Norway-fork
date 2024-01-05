package no.elixir.tsdapimock.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/{project}/files/")
public class FilesController {
  private final FilesService filesService;

  @Autowired
  public FilesController(FilesService filesService) {
    this.filesService = filesService;
  }
}
