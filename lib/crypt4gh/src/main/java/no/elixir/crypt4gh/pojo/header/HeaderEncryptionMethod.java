package no.elixir.crypt4gh.pojo.header;

import java.util.Arrays;
import lombok.AllArgsConstructor;

/**
 * Header encryption methods used to encrypt header packets. For now, only X25519
 * ChaCha20-IETF-Poly1305 is supported.
 */
@AllArgsConstructor
public enum HeaderEncryptionMethod {

  /**
   * Header encrypted with ChaCha20-IETF-Poly1305 using a symmetric key shared via X25519 (Code=0).
   */
  X25519_CHACHA20_IETF_POLY1305(0);

  /**
   * An integer code associated with the HeaderEncryptionMethod. The recognized codes are defined in
   * the Crypt4GH standard specification (section 3.2.1).
   *
   * @see <a href="https://samtools.github.io/hts-specs/crypt4gh.pdf">GA4GH File Encryption
   *     Standard</a>
   */
  private int code;

  /**
   * Returns the integer code associated with the HeaderEncryptionMethod. The recognized codes are
   * defined in the Crypt4GH standard specification (section 3.2.1).
   *
   * @return the integer code for this HeaderEncryptionMethod
   * @see <a href="https://samtools.github.io/hts-specs/crypt4gh.pdf">GA4GH File Encryption
   *     Standard</a>
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the HeaderEncryptionMethod associated with the given integer code. A RunTimeException
   * will be thrown if the code is unknown.
   *
   * @param code an integer code associated with a HeaderEncryptionMethod
   * @return the HeaderEncryptionMethod corresponding to the given integer code
   */
  public static HeaderEncryptionMethod getByCode(int code) {
    return Arrays.stream(HeaderEncryptionMethod.values())
        .filter(i -> i.code == code)
        .findAny()
        .orElseThrow(RuntimeException::new);
  }
}
