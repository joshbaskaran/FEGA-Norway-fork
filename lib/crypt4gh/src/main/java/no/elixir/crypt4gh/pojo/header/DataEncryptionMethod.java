package no.elixir.crypt4gh.pojo.header;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import no.elixir.crypt4gh.pojo.body.ChaCha20IETFPoly1305Segment;
import no.elixir.crypt4gh.pojo.body.Segment;

/**
 * Data encryption methods used to encrypt the actual file data. For now, only
 * ChaCha20-IETF-Poly1305 is supported.
 */
@AllArgsConstructor
public enum DataEncryptionMethod {

  /** Data block encrypted with ChaCha20-IETF-Poly1305 (Code=0) */
  CHACHA20_IETF_POLY1305(
      0,
      ChaCha20IETFPoly1305Segment.NONCE_SIZE
          + Segment.UNENCRYPTED_DATA_SEGMENT_SIZE
          + ChaCha20IETFPoly1305Segment.MAC_SIZE);

  /**
   * An integer code associated with the DataEncryptionMethod. The recognized codes are defined in
   * the Crypt4GH standard specification (section 3.2.2).
   *
   * @see <a href="https://samtools.github.io/hts-specs/crypt4gh.pdf">GA4GH File Encryption
   *     Standard</a>
   */
  private int code;

  /** The size of segment encrypted with the DataEncryptionMethod (in bytes). */
  private int encryptedSegmentSize;

  /**
   * Returns the integer code associated with the DataEncryptionMethod. The recognized codes are
   * defined in the Crypt4GH standard specification (section 3.2.2).
   *
   * @return the integer code for this DataEncryptionMethod
   * @see <a href="https://samtools.github.io/hts-specs/crypt4gh.pdf">GA4GH File Encryption
   *     Standard</a>
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the size of segment encrypted with the DataEncryptionMethod (in bytes). This includes
   * the size of the encrypted data block itself, plus additional information used by the encryption
   * method, such as nonces and MAC codes.
   *
   * @return the size of the encrypted segment
   */
  public int getEncryptedSegmentSize() {
    return encryptedSegmentSize;
  }

  /**
   * Returns the DataEncryptionMethod associated with the given integer code. A RunTimeException
   * will be thrown if the code is unknown.
   *
   * @param code an integer code associated with a DataEncryptionMethod
   * @return the DataEncryptionMethod corresponding to the given integer code
   */
  public static DataEncryptionMethod getByCode(int code) {
    return Arrays.stream(DataEncryptionMethod.values())
        .filter(i -> i.code == code)
        .findAny()
        .orElseThrow(RuntimeException::new);
  }
}
