package ots.cmsdoc.application.util;

import static java.nio.file.Files.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.function.Supplier;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ots.cmsdoc.application.jni.CmsPKCompressor;
import ots.cmsdoc.application.jni.LZWEncoder;
import ots.cmsdoc.application.models.CmsDocument;
import ots.cmsdoc.application.models.CmsDocumentBlobSegment;

public class Compression {

  private static final Logger LOGGER = LoggerFactory.getLogger(Compression.class);

  private static final String COMPRESSION_TYPE_LZW = "01";
  private static final String COMPRESSION_TYPE_PK = "02";
  private static final String COMPRESSION_TYPE_PLAIN = "00";

  public static final String COMPRESSION_TYPE_LZW_FULL = "CWSCMP01";
  public static final String COMPRESSION_TYPE_PK_FULL = "PKWare02";
  public static final String COMPRESSION_TYPE_PLAIN_FULL = "PLAIN_00";
  public static final int BLOB_SEGMENT_LENGTH = 4000;

  protected Supplier<LZWEncoder> lzwSupplier = LZWEncoder::new;
  private CmsDocument doc;

  public Compression(CmsDocument cmsDoc) {
    this.doc = cmsDoc;
  }

  /**
   * Decompress (inflate) a document by determining the compression type, assembling blob segments,
   * and calling appropriate library.
   * 
   *
   * @return base64-encoded String of decompressed document
   */
  public String decompressDoc() {
    String retval = "";

    //TODO: test that doc is not null, or put that test in teh constructor
    //TODO: add tests for each compression method
    if (doc.getCompressionMethod().endsWith(COMPRESSION_TYPE_LZW)) {
      final LZWEncoder lzw = lzwSupplier.get();
      if (!lzw.didLibraryLoad()) {
        LOGGER.warn("LZW COMPRESSION NOT ENABLED!");
      } else {
        retval = decompressLZW();
      }
    } else if (doc.getCompressionMethod().endsWith(COMPRESSION_TYPE_PK)) {
      retval = decompressPK();
    } else if (doc.getCompressionMethod().endsWith(COMPRESSION_TYPE_PLAIN)) {
      retval = decompressPlain();
    } else {
      LOGGER.error("UNSUPPORTED COMPRESSION METHOD! {}", doc.getCompressionMethod());
    }

    return retval;
  }

  /**
   * Decompress (inflate) a PKWare-compressed document by assembling blob segments and calling Java
   * PKWare SDK.
   * 
   * <p>
   * DB2 SQL returns blob segments as hexadecimal using the DB2 {@code blob()} function.
   * </p>
   * 
   * @return base64-encoded String of decompressed document
   */
  private String decompressPK() {
    String retval = "";

    try {
      final ByteArrayBuffer buf = new ByteArrayBuffer(doc.getDocLength().intValue());
      for (CmsDocumentBlobSegment seg : doc.getBlobSegments()) {
        final byte[] blob = seg.getDocBlob();
        buf.append(blob, 0, blob.length);
      }

      final byte[] bytes = new CmsPKCompressor().decompressBytes(buf.buffer());
      LOGGER.debug("DAO: bytes len={}", bytes.length);
      retval = DatatypeConverter.printBase64Binary(bytes);
    } catch (Exception e) {
      errorDecompressing(e);
    }

    return retval;
  }

  /**
   * Decompress (inflate) a non-compressed document by assembling blob segments
   *
   * <p>
   * DB2 SQL returns blob segments as hexadecimal using the DB2 {@code blob()} function.
   * </p>
   *
   * @return base64-encoded String of decompressed document
   */
  protected String decompressPlain() {
    String retval = "";

    try {
      final ByteArrayBuffer buf = new ByteArrayBuffer(doc.getDocLength().intValue());
      byte[] blob;
      for (CmsDocumentBlobSegment seg : doc.getBlobSegments()) {
        blob = seg.getDocBlob();
        buf.append(blob, 0, blob.length);
      }

      final byte[] bytes = buf.buffer();
      LOGGER.debug("DAO: bytes len={}", bytes.length);
      retval = DatatypeConverter.printBase64Binary(bytes);
    } catch (Exception e) {
      errorDecompressing(e);
    }

    return retval;
  }

  /**
   * Decompress (inflate) an LZW-compressed document by assembling blob segments and calling native
   * library.
   * 
   * <p>
   * OPTION: Trap std::exception in shared library and return error code. The LZW library currently
   * returns a blank when decompression fails, for safety, since unhandled C++ exceptions kill the
   * JVM.
   * </p>
   * 
   * <p>
   * For security reasons, remove temporary documents immediately. OPTION: pass bytes to C++ library
   * instead of file names.
   * </p>
   * 
   * @return base64-encoded String of decompressed document
   */
  protected String decompressLZW() {
    String retval = "";

    File src = null;
    File tgt = null;
    try {
      src = File.createTempFile("src", ".lzw");
      src.deleteOnExit();
      tgt = File.createTempFile("tgt", ".doc");
      tgt.deleteOnExit();
    } catch (IOException e) {
      errorDecompressing(e);
    }

    if (src == null || tgt == null) {
      throw new RuntimeException("LZW DE-COMPRESSION ERROR: source and target cannot be null!");
    }

    try {
      blobSegmentsToFile(src);

      final LZWEncoder lzw = lzwSupplier.get();
      lzw.fileCopyUncompress(src.getAbsolutePath(), tgt.getAbsolutePath());
      retval =
          DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(tgt.getAbsolutePath())));

      final boolean srcDeletedSuccessfully = src.delete();
      if (!srcDeletedSuccessfully) {
        LOGGER.warn("Unable to delete compressed file {}", src.getAbsolutePath());
      }

      final boolean tgtDeletedSuccessfully = tgt.delete();
      if (!tgtDeletedSuccessfully) {
        LOGGER.warn("Unable to delete doc file {}", tgt.getAbsolutePath());
      }
    } catch (Exception e) {
      errorDecompressing(e);
    }

    return retval;
  }

  protected void blobSegmentsToFile(File src) {
    try (FileOutputStream fos = new FileOutputStream(src);) {
      for (CmsDocumentBlobSegment seg : doc.getBlobSegments()) {
        final byte[] bytes = seg.getDocBlob();
        fos.write(bytes);
      }
      fos.flush();
    } catch (IOException e) {
      errorDecompressing(e);
    }
  }

  //TODO - write unit test for good locfile path
  //TODO - write unit test for bad locFile path
  //TODO - write unit test for excepting handdling
  public boolean writeFile(Path locFile) {
    //byte[] blobOut = null;
    // convert the encoded string into bytes, and then "undo" the base64 encoding to store as binary
    byte [] blobOut = Base64.getDecoder().decode(decompressDoc().getBytes(StandardCharsets.UTF_8));
    try {
      System.out.println("locFile ID  is " + locFile);
      write(locFile, blobOut);
      return(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return(false);
  }

  protected void errorDecompressing(Exception e) {
    LOGGER.error("ERROR DECOMPRESSING LZW! {}", e.getMessage(), e);
    throw new RuntimeException("ERROR DECOMPRESSING LZW! " + e.getMessage(), e);
  }

  protected void errorCompressing(Exception e) {
    LOGGER.error("ERROR COMPRESSING LZW! {}", e.getMessage(), e);
    throw new RuntimeException("ERROR COMPRESSING LZW! " + e.getMessage(), e);
  }

  public void setLzwSupplier(Supplier<LZWEncoder> lzwSupplier) {
    this.lzwSupplier = lzwSupplier;
  }

}
