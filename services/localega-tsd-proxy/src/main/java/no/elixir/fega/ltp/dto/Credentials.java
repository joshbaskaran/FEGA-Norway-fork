package no.elixir.fega.ltp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/** POJO for CEGA credentials. */
@ToString
@Data
public class Credentials {

  @JsonProperty("passwordHash")
  private String passwordHash;

  @JsonProperty("sshPublicKeys")
  private List<String> publicKeys;
}
