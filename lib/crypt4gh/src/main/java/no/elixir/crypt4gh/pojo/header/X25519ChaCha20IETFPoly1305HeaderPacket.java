package no.elixir.crypt4gh.pojo.header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.elixir.crypt4gh.pojo.EncryptableEntity;
import no.elixir.crypt4gh.util.KeyUtils;
import org.apache.commons.lang3.ArrayUtils;

/** X25519 ChaCha20-IETF-Poly1305 encrypted header packet. */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class X25519ChaCha20IETFPoly1305HeaderPacket extends HeaderPacket
    implements EncryptableEntity {

  /** The name of the cipher used to encrypt this header packet (ChaCha20-Poly1305) */
  public static final String CHA_CHA_20_POLY_1305 = "ChaCha20-Poly1305";

  /** The size of the nonce used by ChaCha20_IETF_Poly1305 (96 bits = 12 bytes) */
  public static final int NONCE_SIZE = 12;

  /** The size of the Message Authentication Code created by Poly1305 (16 bytes) */
  public static final int MAC_SIZE = 16;

  /**
   * Public key of the writer. This is used together with the reader's private key to derive the
   * shared key
   */
  private PublicKey writerPublicKey;

  /** The nonce used in the initialization of the ChaCha20 matrix */
  private byte[] nonce = new byte[NONCE_SIZE];

  /**
   * The encrypted part of the header packet (contains Data Encryption Parameters or Data Edit List)
   */
  private byte[] encryptedPayload;

  /** The Message Authentication Code calculated by Poly1305. */
  private byte[] mac = new byte[MAC_SIZE];

  /**
   * Creates a new header packet with encrypted payload from a supplied payload.
   *
   * <p>The payload, which should be supplied in plaintext, is encrypted with ChaCha20_IETF_Poly1305
   * using an encryption key derived with the X25519 key exchange scheme from the private key of the
   * writer and the public key of the intended reader.
   *
   * @param encryptablePayload the part of the header packet that should be encrypted
   * @param writerPrivateKey the private key of the writer
   * @param readerPublicKey the public key of the intended reader
   * @throws GeneralSecurityException in case of encryption error
   * @throws IOException if the payload cannot be serialized
   */
  public X25519ChaCha20IETFPoly1305HeaderPacket(
      EncryptableHeaderPacket encryptablePayload,
      PrivateKey writerPrivateKey,
      PublicKey readerPublicKey)
      throws GeneralSecurityException, IOException {
    this.packetEncryption = HeaderEncryptionMethod.X25519_CHACHA20_IETF_POLY1305;
    this.writerPublicKey = KeyUtils.getInstance().derivePublicKey(writerPrivateKey);
    this.encryptablePayload = encryptablePayload;
    SecretKey sharedKey =
        KeyUtils.getInstance().generateWriterSharedKey(writerPrivateKey, readerPublicKey);
    encrypt(encryptablePayload.serialize(), sharedKey);
    this.packetLength =
        4 // packetLength length itself
            + 4 // encryption method length
            + 32 // writer public key length
            + NONCE_SIZE
            + encryptedPayload.length
            + MAC_SIZE;
  }

  /**
   * Creates a new header packet from an existing header packet with an encrypted payload.
   *
   * <p>The existing header packet should include the writer's public key, followed by a nonce, the
   * encrypted payload and a Message Authentication Code. The fields read from the provided header
   * packet are used to populate the fields in this class.
   *
   * <p>The encrypted payload will be decrypted (using a shared key derived with X25519 from the
   * original writer's public key included in the header packet and the reader's private key
   * supplied as a parameter), and the decrypted payload will be turned into an
   * EncryptableHeaderPacket and stored in the {@link #encryptablePayload} field in the superclass
   * (in unencrypted form). A GeneralSecurityException will be thrown if the payload was encrypted
   * for a different target recipient and thus cannot be decrypted with the provided private key.
   *
   * @param packetLength the length of the supplied header packet
   * @param headerPacketBody the existing header packet serialized in a byte array
   * @param readerPrivateKey the private key of the intended reader
   * @throws GeneralSecurityException in case of encryption/decryption error
   * @throws IOException if an EncryptableHeaderPacket cannot be created from the decrypted payload
   */
  public X25519ChaCha20IETFPoly1305HeaderPacket(
      int packetLength, byte[] headerPacketBody, PrivateKey readerPrivateKey)
      throws IOException, GeneralSecurityException {
    this.packetEncryption = HeaderEncryptionMethod.X25519_CHACHA20_IETF_POLY1305;
    this.writerPublicKey =
        KeyUtils.getInstance().constructPublicKey(Arrays.copyOfRange(headerPacketBody, 0, 32));
    this.nonce = Arrays.copyOfRange(headerPacketBody, 32, 32 + NONCE_SIZE);
    this.encryptedPayload =
        Arrays.copyOfRange(headerPacketBody, 32 + NONCE_SIZE, headerPacketBody.length - MAC_SIZE);
    this.mac =
        Arrays.copyOfRange(
            headerPacketBody, headerPacketBody.length - MAC_SIZE, headerPacketBody.length);
    this.packetLength = packetLength;
    SecretKey sharedKey =
        KeyUtils.getInstance().generateReaderSharedKey(readerPrivateKey, writerPublicKey);
    byte[] decryptedPayloadBytes = decrypt(sharedKey);
    this.encryptablePayload =
        EncryptableHeaderPacket.create(new ByteArrayInputStream(decryptedPayloadBytes));
  }

  /**
   * Serializes the fields in this header packet to a byte array.
   *
   * <p>The fields are in order: 1. the total size of the header packet (4 byte integer), 2. the
   * public key of the writer (32 bytes), 3. a nonce (16 bytes), 4. the encrypted payload (variable
   * size), 5. a Message Authentication Code (16 bytes).
   *
   * <p>All fields are little-endian.
   *
   * @return a byte array containing the serialized fields of the header packet
   * @throws IOException if any of the fields could not be serialized
   * @throws GeneralSecurityException If the writer's public key could not be encoded
   */
  @Override
  public byte[] serialize() throws IOException, GeneralSecurityException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(packetLength).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(packetEncryption.getCode())
            .array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(32)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(KeyUtils.getInstance().encodeKey(writerPublicKey))
            .array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(NONCE_SIZE).order(ByteOrder.LITTLE_ENDIAN).put(nonce).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(encryptedPayload.length)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(encryptedPayload)
            .array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(MAC_SIZE).order(ByteOrder.LITTLE_ENDIAN).put(mac).array());
    return byteArrayOutputStream.toByteArray();
  }

  /** {@inheritDoc} */
  @Override
  public void encrypt(byte[] unencryptedBytes, SecretKey sharedKey)
      throws GeneralSecurityException {
    SecureRandom.getInstanceStrong().nextBytes(nonce);
    Cipher cipher = Cipher.getInstance(CHA_CHA_20_POLY_1305);
    cipher.init(Cipher.ENCRYPT_MODE, sharedKey, new IvParameterSpec(nonce));
    byte[] encryptedPayloadWithMAC = cipher.doFinal(unencryptedBytes);
    encryptedPayload =
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
    return cipher.doFinal(ArrayUtils.addAll(encryptedPayload, mac));
  }
}
