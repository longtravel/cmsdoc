package ots.cmsdoc.application.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <strong>WARNING: DO NOT CHANGE METHOD SIGNATURES!</strong> Any signature change necessitates
 * regeneration of JNI headers and recompilation of shared libraries!
 * </p>
 * 
 * This JNI native library runs correctly on Linux Jenkins when libLZW.so and libstdc++.so.6 are
 * installed into /usr/local/lib/.
 * 
 * <p>
 * The library does build and run on OS X and Linux environments with current compilers installed.
 * </p>
 * 
 * <p>
 * The following JUnit test runs manually on the clone Jenkins server but not through Gradle on
 * Linux. However, Gradle runs successfully on OS X and Windows. Switch to the Jenkins user with:
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * {@code sudo -u jenkins bash}.
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * Run the JUnit manually with the sample command below. Note that jars are copied manually with the
 * sample script, cp_api_libs.sh.
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * {@code java -Djava.library.path=.:/usr/local/lib/ -cp .:/var/lib/jenkins/workspace/API/build/classes/main:/var/lib/jenkins/workspace/API/build/classes/test:/var/lib/jenkins/workspace/API/build/resources/test:/var/lib/jenkins/test_lib/junit-4.12.jar:/var/lib/jenkins/test_lib/hamcrest-core-1.3.jar:/var/lib/jenkins/test_lib/* org.junit.runner.JUnitCore gov.ca.cwds.rest.util.jni.LZWCompressionTest}
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * <strong>Windows</strong>
 * </p>
 * On Windows systems, use this sample library path: <blockquote>
 * 
 * <pre>
 * {@code -Djava.library.path=.;/Users/joeschmoe/workspace/API/lib;/Windows/System32}
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * On Windows, check the execution permissions of LZW.dll. Verify that "deny" is disabled and that
 * "read an execute" is enabled. Check file properties under security/advanced.
 * </p>
 * 
 * <p>
 * Force JUnit tests to run against native libraries, loaded or not, with JVM argument
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * {@code -Dcwds.jni.force=Y}
 * </pre>
 * 
 * </blockquote>
 * 
 * @author CWDS API Team
 */
public class LZWEncoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(LZWEncoder.class);

  private static final boolean IS_CLASS_LOADED = loadLibs();

  /**
   * <p>
   * Load native library at runtime, when the classloader loads this class. Native libraries follow
   * the naming convention of the host operating system:
   * </p>
   * 
   * <ul>
   * <li>Windows: LZW.dll</li>
   * <li>OS X: libLZW.dylib</li>
   * <li>Linux:libLZW.so</li>
   * </ul>
   * 
   * @return true = native libraries load correctly
   */
  public static boolean loadLibs() {
    LOGGER.info("LZWEncoder: user.dir={}", System.getProperty("user.dir"));
    LOGGER.info("LZWEncoder: java.library.path={}", System.getProperty("java.library.path"));

    final boolean forceLoad = "Y".equalsIgnoreCase(System.getProperty("cwds.jni.force", "N"));
    LOGGER.info("LZWEncoder: cwds.jni.force={}", forceLoad);

    boolean retval = false;

    try {
      System.loadLibrary("LZW");
      retval = true;
    } catch (UnsatisfiedLinkError e) {
      retval = false;
      LOGGER.error("Failed to load LZW library", e);
    }

    if (!retval && forceLoad) {
      retval = true;
    }

    return retval;
  }

  /**
   * Deflates a file compressed with LZW variable 15 bit.
   * 
   * @param src source file name to compress
   * @param tgt target file name of compressed result
   */
  public native void fileCopyCompress(String src, String tgt);

  /**
   * Inflates a file compressed with LZW variable 15 bit.
   * 
   * @param src source file name to decompress
   * @param tgt target file name of decompressed result
   */
  public native void fileCopyUncompress(String src, String tgt);

  /**
   * Convenience method calls static {@link #isClassloaded()}. Easier to mock with Mockito framework
   * than a static call.
   * 
   * @return when the LZW shared library loaded correctly
   */
  public boolean didLibraryLoad() {
    return LZWEncoder.isClassloaded();
  }

  /**
   * Some JUnit tests may not run in all environments, if native libraries did not load correctly.
   * 
   * @return whether dependent native libraries loaded correctly
   */
  public static boolean isClassloaded() {
    return IS_CLASS_LOADED;
  }

}


