package no.elixir.crypt4gh.pojo.header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** ChaCha20-IETF-Poly1305 Data Encryption Parameters. */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class ChaCha20IETFPoly1305EncryptionParameters extends DataEncryptionParameters {

  /** The name of the algorithm to be associated with the secret key */
  public static final String CHA_CHA_20 = "ChaCha20";

  /** The secret symmetric key used to encrypt the actual file data (aka Data Key or K_data) */
  private SecretKey dataKey;

  /**
   * Creates a new DataEncryptionParameters object based on the ChaCha20-Poly1305 encryption method
   * with the provided key.
   *
   * @param dataKey the symmetric key used to encrypt the actual file data
   */
  public ChaCha20IETFPoly1305EncryptionParameters(SecretKey dataKey) {
    this.packetType = HeaderPacketType.DATA_ENCRYPTION_PARAMETERS;
    this.dataEncryptionMethod = DataEncryptionMethod.CHACHA20_IETF_POLY1305;
    this.dataKey = dataKey;
  }

  /**
   * Creates a new DataEncryptionParameters object based on the ChaCha20-Poly1305 encryption method
   * with a 256-bits key read from the provided input stream.
   *
   * @param inputStream a stream from which the 256-bits data encryption key can be read
   */
  ChaCha20IETFPoly1305EncryptionParameters(InputStream inputStream) throws IOException {
    this.packetType = HeaderPacketType.DATA_ENCRYPTION_PARAMETERS;
    this.dataEncryptionMethod = DataEncryptionMethod.CHACHA20_IETF_POLY1305;
    this.dataKey = new SecretKeySpec(inputStream.readNBytes(32), CHA_CHA_20);
  }

  /**
   * Serializes this DataEncryptionParameters object to a byte array.
   *
   * @return a byte array containing the fields of this DataEncryptionParameters object
   */
  @Override
  public byte[] serialize() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(packetType.getCode()).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(dataEncryptionMethod.getCode())
            .array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN).put(dataKey.getEncoded()).array());
    return byteArrayOutputStream.toByteArray();
  }
}
