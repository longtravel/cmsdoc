package ots.cmsdoc.application.jni;

import com.pkware.deflate.DeflateOutputStream;
import com.pkware.deflate.InflateInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compresses (deflates) and decompresses (inflates) PK archives created by the Windows PKWare
 * library.
 * 
 * <p>
 * <strong>NOTE: </strong>This class only works with PK-compressed docs, not the LZW variable 15-bit
 * algorithm. For the latter, see {@link LZWEncoder}.
 * </p>
 *
 * <p>
 * This class provides specialized methods to compress/decompress documents for various inputs,
 * including files, streams, byte arrays, base64-encoded strings, and hexadecimal strings.
 * </p>
 *
 * <p>
 * For some unknown reason, this particular compression algorithm (PK_COMPRESS_METHOD_DEFLATE, level
 * 6) <strong>does not require a current license key</strong>. For example, this initialization is
 * not needed:
 * </p>
 * 
 * <pre>
 * <code>PKSession session = new PKSession(PKWARE_LICENSE_KEY);</code>
 * </pre>
 * 
 * <strong>Java PKWare SDK details:</strong>
 * <ul>
 * <li>Name: com/pkware/archive</li>
 * <li>Implementation-Vendor: PKWARE, Inc.</li>
 * <li>Specification-Title: SecureZIP Toolkit for Java</li>
 * <li>Implementation-Title: com.pkware.archive</li>
 * <li>Implementation-Version: 3.20</li>
 * <li>Specification-Version: 3.20</li>
 * <li>Specification-Vendor: PKWARE, Inc.</li>
 * </ul>
 * 
 * @author CWDS API Team
 * @see LZWEncoder
 */
public class CmsPKCompressor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CmsPKCompressor.class);

  private static final int DEFAULT_COMPRESSION_LEVEL = 6;
  private static final int DEFAULT_OUTPUT_SIZE = 0x10000;

  /**
   * Constructor
   */
  public CmsPKCompressor() {
    // Default, no-op. Get over it, SonarQube.
  }

  /**
   * Decompress (inflate) a CMS PKWare archive.
   * 
   * @param inputFileName file name of resulting decompressed output
   * @param outputFileName file name to decompress
   * @throws IOException If an I/O error occurs
   */
  public void decompressFile(String inputFileName, String outputFileName) throws IOException {
    if (StringUtils.isBlank(inputFileName) || StringUtils.isBlank(outputFileName)) {
      throw new IOException("REQUIRED: file names cannot be null");
    }

    try (final FileInputStream fis = new FileInputStream(createFile(inputFileName));
        final InputStream iis = new InflateInputStream(fis, true);
        final FileOutputStream fos = new FileOutputStream(createFile(outputFileName));) {
      IOUtils.copy(iis, fos);
    } catch (Exception e) {
      throw new IOException("Error copying file", e);
    }
  }

  /**
   * Decompress (inflate) raw bytes of a PK-compressed document.
   * 
   * @param bytes raw bytes of PK-compressed document.
   * @return byte array of decompressed document
   * @throws IOException If an I/O error occurs
   */
  public byte[] decompressBytes(byte[] bytes) throws IOException {
    if (bytes == null) {
      throw new IOException("REQUIRED: bytes to decompress cannot be null");
    }

    final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    final InputStream iis = new InflateInputStream(bis, true);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_OUTPUT_SIZE);

    IOUtils.copy(iis, bos);
    iis.close();
    bis.close();
    bos.flush();
    bos.close();

    final byte[] retval = bos.toByteArray();
    LOGGER.debug("CmsPKCompressor.decompress(byte[]): retval len={}", retval.length);
    return retval;
  }

  /**
   * Decompress (inflate) an InputStream of a PK-compressed document.
   * 
   * @param input InputStream of PK-compressed document.
   * @return byte array of decompressed document
   * @throws IOException If an I/O error occurs
   */
  public byte[] decompressStream(InputStream input) throws IOException {
    if (input == null) {
      throw new IOException("REQUIRED: input stream to decompress cannot be null");
    }

    final InputStream iis = new InflateInputStream(input, true);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_OUTPUT_SIZE);
    IOUtils.copy(iis, bos);

    iis.close();
    bos.flush();
    bos.close();
    return bos.toByteArray();
  }

  /**
   * Convenience method. Decompress (inflate) a base64-encoded string of a PK-compressed archive.
   * 
   * @param base64Doc base64-encoded, PK-compressed archive
   * @return raw byte array of decompressed document
   * @throws IOException If an I/O error occurs
   */
  public byte[] decompressBase64(String base64Doc) throws IOException {
    if (base64Doc == null) {
      throw new IOException("REQUIRED: base64 string to decompress cannot be null");
    }
    final byte[] bytes = decompressBytes(DatatypeConverter.parseBase64Binary(base64Doc.trim()));
    LOGGER.debug("CmsPKCompressor.decompressBase64(String): bytes len={}", bytes.length);
    return bytes;
  }

  /**
   * Convenience method. Decompress (inflate) a hexadecimal string of a PK-compressed archive.
   * 
   * @param hex base64-encoded, PK-compressed archive
   * @return raw byte array of decompressed document
   * @throws IOException If an I/O error occurs
   */
  public byte[] decompressHex(String hex) throws IOException {
    if (StringUtils.isBlank(hex)) {
      throw new IOException("REQUIRED: hex to decompress cannot be null");
    }

    final byte[] bytes = decompressBytes(DatatypeConverter.parseHexBinary(hex.trim()));
    LOGGER.debug("CmsPKCompressor.decompressHex(String): bytes len={}", bytes.length);
    return bytes;
  }

  /**
   * Compress (deflate) a CMS PKWare archive and writes resulting compressed document to given
   * output file.
   * 
   * @param inputFileName file name to decompress
   * @param outputFileName file name of resulting decompressed output
   * @throws IOException If an I/O error occurs
   */
  public void compressFile(String inputFileName, String outputFileName) throws IOException {
    if (StringUtils.isBlank(inputFileName) || StringUtils.isBlank(outputFileName)) {
      throw new IOException("REQUIRED: file names cannot be null");
    }

    try (final FileInputStream fis = new FileInputStream(createFile(inputFileName));
        final OutputStream fos = new DeflateOutputStream(
            new FileOutputStream(createFile(outputFileName)), DEFAULT_COMPRESSION_LEVEL, true);) {
      IOUtils.copy(fis, fos);
    } catch (Exception e) {
      throw new IOException("Unable to copy file", e);
    }
  }

  public String compressBase64ToHex(String base64) throws IOException {
    return DatatypeConverter
        .printHexBinary(compressBytes(DatatypeConverter.parseBase64Binary(base64))).toLowerCase();
  }

  /**
   * Convenience method. Compress (deflate) a document InputStream.
   * 
   * @param bytes raw bytes of the document to compress
   * @return raw byte array of compressed document
   * @throws IOException If an I/O error occurs
   */
  public byte[] compressBytes(byte[] bytes) throws IOException {
    if (bytes == null) {
      throw new IOException("REQUIRED: bytes to compress cannot be null");
    }

    final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_OUTPUT_SIZE);
    final OutputStream dos = new DeflateOutputStream(bos, DEFAULT_COMPRESSION_LEVEL, true);

    IOUtils.copy(bis, dos);
    bis.close();
    dos.close();

    return bos.toByteArray();
  }

  private File createFile(String file) {
    return new File(FilenameUtils.getFullPath(file), FilenameUtils.getName(file)); // NOSONAR
  }

}
