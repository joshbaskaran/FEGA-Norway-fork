package no.elixir.crypt4gh.pojo.header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import no.elixir.crypt4gh.pojo.Crypt4GHEntity;

/** Crypt4GH header containing both unencrypted and encrypted payloads. */
@ToString
@AllArgsConstructor
public class Header implements Crypt4GHEntity {

  /**
   * The size of the header when not counting the individual header packets. This includes the magic
   * word "crypt4gh" (8 bytes), the version number (4 byte integer) and the header packet count (4
   * byte integer)
   */
  public static final int UNENCRYPTED_HEADER_LENGTH = 8 + 4 + 4;

  /** The magic word "crypt4gh" used at the start of a Crypt4GH file to identify it as such. */
  public static final String MAGIC_WORD = "crypt4gh";

  /** The version number of the Crypt4GH standard supported by this implementation. */
  public static final int VERSION = 1;

  /** The list of header packets included in the header. */
  @Getter private final List<HeaderPacket> headerPackets;

  /**
   * Reads a header from an input stream. The header packets that can be decrypted with the provided
   * private key are added to the headerPackets list.
   *
   * @param inputStream a stream to read the header from
   * @param readerPrivateKey the private key of the reader
   * @throws IOException if something goes wrong while reading from the input stream
   * @throws GeneralSecurityException if the input stream does not contain a valid Crypt4GH file or
   *     the file has an unsupported version number.
   */
  public Header(InputStream inputStream, PrivateKey readerPrivateKey)
      throws IOException, GeneralSecurityException {
    byte[] unencryptedHeaderBytes = inputStream.readNBytes(UNENCRYPTED_HEADER_LENGTH);
    String magicWord = new String(Arrays.copyOfRange(unencryptedHeaderBytes, 0, 8));
    if (!MAGIC_WORD.equals(magicWord)) {
      throw new GeneralSecurityException("Not a Crypt4GH stream");
    }
    int version = Crypt4GHEntity.getInt(Arrays.copyOfRange(unencryptedHeaderBytes, 8, 12));
    if (VERSION != version) {
      throw new GeneralSecurityException("Unsupported Crypt4GH version: " + version);
    }
    int headerPacketCount =
        Crypt4GHEntity.getInt(Arrays.copyOfRange(unencryptedHeaderBytes, 12, 16));
    this.headerPackets = new ArrayList<>();
    for (int i = 0; i < headerPacketCount; i++) {
      Optional<HeaderPacket> headerPacketOptional =
          HeaderPacket.create(inputStream, readerPrivateKey);
      headerPacketOptional.ifPresent(headerPackets::add);
    }
  }

  /**
   * Returns a collection of all the Data Encryption Parameters packets found in this header.
   *
   * @return a collection of Data Encryption Parameters packets
   * @throws GeneralSecurityException if no Data Encryption Parameters packets can be found in the
   *     header
   */
  public Collection<DataEncryptionParameters> getDataEncryptionParametersList()
      throws GeneralSecurityException {
    Collection<DataEncryptionParameters> result = new ArrayList<>();
    for (HeaderPacket headerPacket : headerPackets) {
      EncryptableHeaderPacket encryptablePayload = headerPacket.getEncryptablePayload();
      HeaderPacketType packetType = encryptablePayload.getPacketType();
      if (packetType == HeaderPacketType.DATA_ENCRYPTION_PARAMETERS) {
        result.add((DataEncryptionParameters) encryptablePayload);
      }
    }
    if (result.isEmpty()) {
      throw new GeneralSecurityException("Data Encryption Parameters not found in the Header");
    }
    return result;
  }

  /** Removes all the Data Edit List packets from the header. */
  public void removeDataEditList() {
    Iterator<HeaderPacket> iterator = headerPackets.iterator();
    while (iterator.hasNext()) {
      HeaderPacket headerPacket = iterator.next();
      EncryptableHeaderPacket encryptablePayload = headerPacket.getEncryptablePayload();
      HeaderPacketType packetType = encryptablePayload.getPacketType();
      if (packetType == HeaderPacketType.DATA_EDIT_LIST) {
        iterator.remove();
      }
    }
  }

  /**
   * Returns a Data Edit List packet found in the header.
   *
   * @return an Optional containing a Data Edit List, provided that one was found in this header
   */
  public Optional<DataEditList> getDataEditList() {
    for (HeaderPacket headerPacket : headerPackets) {
      EncryptableHeaderPacket encryptablePayload = headerPacket.getEncryptablePayload();
      HeaderPacketType packetType = encryptablePayload.getPacketType();
      if (packetType == HeaderPacketType.DATA_EDIT_LIST) {
        return Optional.of((DataEditList) encryptablePayload);
      }
    }
    return Optional.empty();
  }

  /**
   * Serializes the complete header into a byte array. The header consists of the magic word
   * "crypt4gh" followed by a version number, a number counting the header packets, and finally all
   * of the individual header packets.
   *
   * @return a byte array containing the serialized header
   * @throws IOException if some of the header packets cannot be serialized
   * @throws GeneralSecurityException if some of the header packet payloads cannot be encrypted
   */
  @Override
  public byte[] serialize() throws IOException, GeneralSecurityException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(
        ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).put(MAGIC_WORD.getBytes()).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(VERSION).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(headerPackets.size()).array());
    for (HeaderPacket headerPacket : headerPackets) {
      byteArrayOutputStream.write(headerPacket.serialize());
    }
    return byteArrayOutputStream.toByteArray();
  }
}
