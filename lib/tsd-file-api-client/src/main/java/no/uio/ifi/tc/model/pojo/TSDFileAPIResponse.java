package no.uio.ifi.tc.model.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TSDFileAPIResponse {

  private int statusCode;
  private String statusText;
}
