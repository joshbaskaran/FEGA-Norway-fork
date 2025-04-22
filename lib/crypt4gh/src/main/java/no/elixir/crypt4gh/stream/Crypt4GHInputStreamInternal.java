package no.elixir.crypt4gh.stream;

import static no.elixir.crypt4gh.pojo.body.Segment.UNENCRYPTED_DATA_SEGMENT_SIZE;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.elixir.crypt4gh.pojo.body.Segment;
import no.elixir.crypt4gh.pojo.header.DataEditList;
import no.elixir.crypt4gh.pojo.header.DataEncryptionParameters;
import no.elixir.crypt4gh.pojo.header.Header;

/** Internal part of Crypt4GHInputStream that wraps existing InputStream. Not a public API. */
@Slf4j
class Crypt4GHInputStreamInternal extends FilterInputStream {

  /** The header read from the input stream */
  private Header header;

  /**
   * A buffer to store data read from the input stream. This will be passed on to the outer
   * Crypt4GHInputStream
   */
  private int[] buffer;

  /**
   * The number of bytes the outer Crypt4GHInputStream has read from the internal buffer in this
   * class
   */
  private int bytesRead;

  /** The Data Encryption Parameters read from the header */
  private Collection<DataEncryptionParameters> dataEncryptionParametersList;

  /** A Data Edit List to apply to the data (if specified in the header) */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private Optional<DataEditList> dataEditList;

  /** The size of each encrypted segment (according to the encryption method used in this file) */
  private int encryptedSegmentSize;

  /** A counter to keep track of the last decrypted segment */
  private long lastDecryptedSegment = -1;

  /**
   * Constructs the internal part of Crypt4GHInputStream that wraps existing InputStream. Not a
   * public API.
   *
   * @param in the stream to read the Crypt4GH file from
   * @param readerPrivateKey the private key of the intended recipient
   * @throws IOException if something goes wrong while reading from the stream
   * @throws GeneralSecurityException if the header contains several Data Encryption Parameters
   *     specifying different encryption methods
   */
  Crypt4GHInputStreamInternal(InputStream in, PrivateKey readerPrivateKey)
      throws IOException, GeneralSecurityException {
    super(in);
    this.header = new Header(in, readerPrivateKey);
    this.dataEncryptionParametersList = header.getDataEncryptionParametersList();
    DataEncryptionParameters firstDataEncryptionParameters =
        dataEncryptionParametersList.iterator().next();
    for (DataEncryptionParameters encryptionParameters : dataEncryptionParametersList) {
      if (firstDataEncryptionParameters.getDataEncryptionMethod()
          != encryptionParameters.getDataEncryptionMethod()) {
        throw new GeneralSecurityException("Different Data Encryption Methods are not supported");
      }
    }
    this.encryptedSegmentSize =
        firstDataEncryptionParameters.getDataEncryptionMethod().getEncryptedSegmentSize();
    this.dataEditList = header.getDataEditList();
  }

  /**
   * Returns the Data Edit List from the header (if present).
   *
   * @return an Optional that may contain a Data Edit List, if one was present in the header
   */
  Optional<DataEditList> getDataEditList() {
    return dataEditList;
  }

  /**
   * Gets header.
   *
   * @return Crypt4GH full header.
   */
  Header getHeader() {
    return header;
  }

  /** {@inheritDoc} */
  @Override
  public int read() throws IOException {
    if (buffer == null || buffer.length == bytesRead) {
      fillBuffer();
    }
    if (buffer == null || buffer.length == 0) {
      return -1;
    } else {
      return buffer[bytesRead++];
    }
  }

  /** {@inheritDoc} */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    /*
       Reusing default `InputStream`'s implementation, because `FilterStream`'s implementation doesn't fit
    */
    Objects.checkFromIndexSize(off, len, b.length);
    if (len == 0) {
      return 0;
    }

    int c = read();
    if (c == -1) {
      return -1;
    }
    b[off] = (byte) c;

    int i = 1;
    try {
      for (; i < len; i++) {
        c = read();
        if (c == -1) {
          break;
        }
        b[off + i] = (byte) c;
      }
    } catch (IOException ee) {
      log.error(ee.getMessage(), ee);
    }
    return i;
  }

  /** {@inheritDoc} */
  @Override
  public long skip(long n) throws IOException {
    if (n <= 0) {
      return 0;
    }
    if (buffer == null || buffer.length == bytesRead) {
      fillBuffer();
    }
    long currentDecryptedPosition =
        lastDecryptedSegment * UNENCRYPTED_DATA_SEGMENT_SIZE + bytesRead;
    long newDecryptedPosition = currentDecryptedPosition + n;
    long newSegmentNumber = newDecryptedPosition / UNENCRYPTED_DATA_SEGMENT_SIZE;
    if (newSegmentNumber != lastDecryptedSegment) {
      long segmentsToSkip = newSegmentNumber - lastDecryptedSegment - 1;
      skipSegments(segmentsToSkip);
      fillBuffer();
      currentDecryptedPosition = lastDecryptedSegment * UNENCRYPTED_DATA_SEGMENT_SIZE;
    }
    long delta = newDecryptedPosition - currentDecryptedPosition;
    if (bytesRead + delta > buffer.length) {
      long missingBytes = bytesRead + delta - buffer.length;
      assert bytesRead + delta - missingBytes <= Integer.MAX_VALUE
          : "Value assigned to int exceeds integer range";
      bytesRead = (int) (bytesRead + delta - missingBytes);
      return n - missingBytes;
    }
    assert bytesRead + delta <= Integer.MAX_VALUE : "Value assigned to int exceeds integer range";
    bytesRead = (int) (bytesRead + delta);
    return n;
  }

  /**
   * Skips ahead a number of segments (data blocks) in the stream.
   *
   * @param n the number of segments to skip
   * @throws IOException if something goes wrong while skipping ahead in the stream
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  private synchronized void skipSegments(long n) throws IOException {
    in.skip(n * encryptedSegmentSize);
    lastDecryptedSegment += n;
  }

  /**
   * Reads an encrypted segment (data block) from the wrapped input stream, decrypts it and places
   * the data in the internal buffer.
   *
   * @throws IOException if something goes wrong while reading from the stream
   */
  private synchronized void fillBuffer() throws IOException {
    try {
      byte[] encryptedSegmentBytes = in.readNBytes(encryptedSegmentSize);
      if (encryptedSegmentBytes.length > 0) {
        decryptSegment(encryptedSegmentBytes);
      } else if (buffer != null) {
        Arrays.fill(buffer, -1);
      }
      bytesRead = 0;
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Decrypts an encrypted segment provided as a byte array. The decrypted data is placed in the
   * internal buffer and the lastDecryptedSegment counter is incremented.
   *
   * @param encryptedSegmentBytes an encrypted segment
   * @throws GeneralSecurityException if the segment could not be decrypted with any of the known
   *     keys
   */
  private synchronized void decryptSegment(byte[] encryptedSegmentBytes)
      throws GeneralSecurityException {
    Segment segment = Segment.create(encryptedSegmentBytes, dataEncryptionParametersList);
    byte[] unencryptedData = segment.getUnencryptedData();
    buffer = new int[unencryptedData.length];
    for (int i = 0; i < unencryptedData.length; i++) {
      buffer[i] = unencryptedData[i] & 0xff;
    }
    lastDecryptedSegment++;
  }
}
