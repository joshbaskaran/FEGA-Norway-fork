package no.elixir.crypt4gh.pojo.header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.elixir.crypt4gh.pojo.Crypt4GHEntity;

/** Data Edit List. */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class DataEditList extends EncryptableHeaderPacket {

  /** The size of the "lengths" array */
  private int numberLengths;

  /** A list of number of bytes to alternatingly discard and keep from the plaintext data */
  private long[] lengths;

  /**
   * Creates a new Data Edit List from an array of byte counts. The counts specify how many bytes to
   * alternatingly discard and keep from the plaintext data. (i.e. discard the first number of
   * bytes, then keep the next number of bytes, discard the third number of bytes, and keep the
   * fourth number of bytes, etc.)
   *
   * @param lengths an array of byte counts
   */
  public DataEditList(long[] lengths) {
    this.packetType = HeaderPacketType.DATA_EDIT_LIST;
    this.numberLengths = lengths.length;
    this.lengths = lengths;
  }

  /**
   * Reads a Data Edit List from an input stream. The stream should start with a 4-byte integer
   * (little-endian) that specifies the number of byte counts in the list. This is then followed by
   * the actual byte counts represented as 8-byte longs (also little-endian).
   *
   * @throws IOException if something goes wrong while reading from the stream
   */
  DataEditList(InputStream inputStream) throws IOException {
    this.packetType = HeaderPacketType.DATA_EDIT_LIST;
    this.numberLengths = Crypt4GHEntity.getInt(inputStream.readNBytes(4));
    this.lengths = new long[numberLengths];
    for (int i = 0; i < numberLengths; i++) {
      lengths[i] = Crypt4GHEntity.getLong(inputStream.readNBytes(8));
    }
  }

  /**
   * Serializes this Data Edit List into a byte array. The serialized form starts with the packet
   * type code (4-byte integer), followed by the number of byte counts (4-byte integer), and finally
   * the byte counts themselves (each a 8-byte long). All values are little-endian.
   *
   * @return a byte array containing the serialized Data Edit List
   * @throws IOException if something goes wrong with the serialization
   */
  @Override
  public byte[] serialize() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(packetType.getCode()).array());
    byteArrayOutputStream.write(
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(numberLengths).array());
    for (long length : lengths) {
      byteArrayOutputStream.write(
          ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(length).array());
    }
    return byteArrayOutputStream.toByteArray();
  }
}
