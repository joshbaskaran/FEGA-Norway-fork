package no.elixir.crypt4gh.pojo.header;

import java.util.Arrays;
import lombok.AllArgsConstructor;

/**
 * Header packet types, for now only two available: Data Encryption Parameters and Data Edit List.
 */
@AllArgsConstructor
public enum HeaderPacketType {
  /** Header packet for Data Encryption Parameters (code=0). */
  DATA_ENCRYPTION_PARAMETERS(0),
  /** Header packet for Data Edit List (code=1). */
  DATA_EDIT_LIST(1);

  /**
   * An integer code associated with the HeaderPacketType. The recognized codes are defined in the
   * Crypt4GH standard specification (section 3.2.2).
   *
   * @see <a href="https://samtools.github.io/hts-specs/crypt4gh.pdf">GA4GH File Encryption
   *     Standard</a>
   */
  private int code;

  /**
   * Returns the integer code associated with the HeaderPacketType. The recognized codes are defined
   * in the Crypt4GH standard specification (section 3.2.2).
   *
   * @return the integer code for this HeaderPacketType
   * @see <a href="https://samtools.github.io/hts-specs/crypt4gh.pdf">GA4GH File Encryption
   *     Standard</a>
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the HeaderPacketType associated with the given integer code. A RunTimeException will be
   * thrown if the code is unknown.
   *
   * @param code an integer code associated with a HeaderPacketType
   * @return the HeaderPacketType corresponding to the given integer code
   */
  public static HeaderPacketType getByCode(int code) {
    return Arrays.stream(HeaderPacketType.values())
        .filter(i -> i.code == code)
        .findAny()
        .orElseThrow(RuntimeException::new);
  }
}
