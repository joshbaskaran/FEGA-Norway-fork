package no.uio.ifi.tc.model.pojo;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ResumableUploads extends TSDFileAPIResponse {

  private List<ResumableUpload> resumables;
}
