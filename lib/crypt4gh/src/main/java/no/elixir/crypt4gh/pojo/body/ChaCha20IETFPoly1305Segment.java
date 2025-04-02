package no.elixir.crypt4gh.pojo.body;

import static no.elixir.crypt4gh.pojo.header.X25519ChaCha20IETFPoly1305HeaderPacket.CHA_CHA_20_POLY_1305;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.elixir.crypt4gh.pojo.EncryptableEntity;
import no.elixir.crypt4gh.pojo.header.ChaCha20IETFPoly1305EncryptionParameters;
import org.apache.commons.lang3.ArrayUtils;

/** Data segment, ChaCha20 encrypted, 65564 bytes long (according to the current spec). */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class ChaCha20IETFPoly1305Segment extends Segment implements EncryptableEntity {

  /** Size of the nonce used by ChaCha20 (IETF version uses a 12 bytes = 96 bits nonce) */
  public static final int NONCE_SIZE = 12;

  /** Size of the Message Authentication Code returned by Poly1305 (16 bytes) */
  public static final int MAC_SIZE = 16;

  /** The nonce used in the initialization of the ChaCha20 matrix */
  private byte[] nonce = new byte[NONCE_SIZE];

  /** The encrypted data in this segment */
  private byte[] encryptedData;

  /** The Message Authentication Code calculated by Poly1305 from the encrypted segment */
  private byte[] mac = new byte[MAC_SIZE];

  /**
   * Creates a new segment based on the ChaCha20-Poly1305 encryption cipher from a given data block.
   * The provided data block may be unencrypted or encrypted. If the data block is already
   * encrypted, it should start with a nonce followed by the encrypted data and end with a MAC
   * value.
   *
   * @param data the segment data (either encrypted or unencrypted)
   * @param dataEncryptionParameters containing the ChaCha20 data encryption key
   * @param encrypt if {@code true}, the data is expected to be unencrypted, and it will then be
   *     encrypted with a randomly generated nonce. if {@code false}, the data is expected to be
   *     encrypted already, and it will then be deserialized and decrypted.
   * @throws GeneralSecurityException if something goes wrong during encryption/decryption
   */
  ChaCha20IETFPoly1305Segment(
      byte[] data,
      ChaCha20IETFPoly1305EncryptionParameters dataEncryptionParameters,
      boolean encrypt)
      throws GeneralSecurityException {
    if (encrypt) {
      this.unencryptedData = data;
      encrypt(data, dataEncryptionParameters.getDataKey());
    } else {
      this.nonce = Arrays.copyOfRange(data, 0, NONCE_SIZE);
      this.encryptedData = Arrays.copyOfRange(data, NONCE_SIZE, data.length - MAC_SIZE);
      this.mac = Arrays.copyOfRange(data, data.length - MAC_SIZE, data.length);
      this.unencryptedData = decrypt(dataEncryptionParameters.getDataKey());
    }
  }

  /**
   * Serializes the fields in this segment into a byte array. The serialized segment will contain a
   * nonce, followed by the encrypted data and end with a MAC value. All fields are stored in
   * little-endian format.
   *
   * @return a byte array containing the encrypted data block
   * @throws IOException if any of the fields cannot be serialized
   */
  @Override
  public byte[] serialize() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(
        ByteBuffer.allocate(NONCE_SIZE).order(ByteOrder.LITTLE_ENDIAN).put(nonce).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(encryptedData.length)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(encryptedData)
            .array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(MAC_SIZE).order(ByteOrder.LITTLE_ENDIAN).put(mac).array());
    return byteArrayOutputStream.toByteArray();
  }

  /** {@inheritDoc} */
  @Override
  public void encrypt(byte[] unencryptedData, SecretKey sharedKey) throws GeneralSecurityException {
    SecureRandom.getInstanceStrong().nextBytes(nonce);
    Cipher cipher = Cipher.getInstance(CHA_CHA_20_POLY_1305);
    cipher.init(Cipher.ENCRYPT_MODE, sharedKey, new IvParameterSpec(nonce));
    byte[] encryptedPayloadWithMAC = cipher.doFinal(unencryptedData);
    encryptedData =
        Arrays.copyOfRange(encryptedPayloadWithMAC, 0, encryptedPayloadWithMAC.length - MAC_SIZE);
    mac =
        Arrays.copyOfRange(
            encryptedPayloadWithMAC,
            encryptedPayloadWithMAC.length - MAC_SIZE,
            encryptedPayloadWithMAC.length);
  }

  /** {@inheritDoc} */
  @Override
  public byte[] decrypt(SecretKey sharedKey) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(CHA_CHA_20_POLY_1305);
    cipher.init(Cipher.DECRYPT_MODE, sharedKey, new IvParameterSpec(nonce));
    return cipher.doFinal(ArrayUtils.addAll(encryptedData, mac));
  }
}
