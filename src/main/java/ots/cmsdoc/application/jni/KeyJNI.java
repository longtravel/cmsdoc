package ots.cmsdoc.application.jni;

//import gov.ca.cwds.data.persistence.cms.StaffPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//
// http://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
//

// COMPILE JAVA:
// javac gov/ca/cwds/rest/util/jni/KeyJNI.java

// GENERATE C HEADERS:
// javah -jni gov.ca.cwds.rest.util.jni.KeyJNI

// JAVA EXECUTE: OS X:
// java -Djava.library.path=.:/usr/local/lib/ gov.ca.cwds.rest.util.jni.KeyJNI

/**
 * Calls native CWDS key generation library via JNI.
 *
 * <p>
 * <strong>WARNING: DO NOT CHANGE METHOD SIGNATURES!</strong> Any signature change necessitates
 * regeneration of JNI headers and recompilation of shared libraries!
 * </p>
 * 
 * <p>
 * <strong>"You break it, you buy it."</strong>
 * </p>
 * 
 * <h2>Shared libraries by platform</h2>
 * <table summary="">
 * <tr>
 * <th align="justify">Platform</th>
 * <th align="justify">Compiler</th>
 * <th align="justify">Shared library</th>
 * </tr>
 * <tr>
 * <td align="justify">Windows</td>
 * <td align="justify">MinGW 64</td>
 * <td align="justify">KeyJNI.dll</td>
 * </tr>
 * <tr>
 * <td>Linux</td>
 * <td>GNU g++ 5</td>
 * <td>libKeyJNI.so</td>
 * </tr>
 * <tr>
 * <td>OS X</td>
 * <td>Clang</td>
 * <td>libKeyJNI.dylib</td>
 * </tr>
 * </table>
 * <h2>Steps to build and run</h2>
 * 
 * @author CWDS API Team
 */

public final class KeyJNI {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyJNI.class);

  private static final boolean IS_CLASS_LOADED = loadLibs();

  /**
   * Load native library at runtime, when the classloader loads this class. Native libraries follow
   * the naming convention of the host operating system:
   * 
   * <p>
   * <ul>
   * <li>Windows: KeyJNI.dll</li>
   * <li>OS X: libKeyJNI.dylib</li>
   * <li>Linux: libKeyJNI.so</li>
   * </ul>
   * </p>
   * 
   * @return true = native libraries load correctly
   */
  private static final boolean loadLibs() {
    LOGGER.info("user.dir={}", System.getProperty("user.dir"));
    LOGGER.info("java.library.path={}", System.getProperty("java.library.path"));

    final boolean forceLoad = "Y".equalsIgnoreCase(System.getProperty("cwds.jni.force", "N"));
    LOGGER.info("cwds.jni.force={}", forceLoad);

    boolean retval = false;

    try {
      System.loadLibrary("KeyJNI");
      retval = true;
      LOGGER.info("KeyJNI library loaded successfully");
    } catch (UnsatisfiedLinkError e) {
      retval = false;
      LOGGER.error("KeyJNI library failed to load!", e);
    }

    if (!retval && forceLoad) { // NOSONAR
      retval = true;
    }

    return retval;
  }

  /**
   * Utility struct class stores details of CWDS key decomposition.
   * 
   * <p>
   * Intentionally oversimplified structure for C++ results. Must match function signature exported
   * in shared library. <strong>DO NOT CHANGE!</strong>
   * </p>
   */
  public static final class KeyDetail {
    public String key; // NOSONAR
    public String staffId; // NOSONAR
    public String UITimestamp; // NOSONAR
    public String PTimestamp; // NOSONAR
  }

  /**
   * Tells whether the JVM successfully loaded the shared library for the target platform.
   * 
   * @return whether the JNI shared library loaded successfully
   */
  public static final boolean isClassloaded() {
    return IS_CLASS_LOADED;
  }

  /**
   * Generates a unique key for use within CWDS, derived from a staff person id.
   * 
   * @param staffId
   * @return The generated key
   */
  public native String generateKey(String staffId);

  /**
   * Decomposes a CMS, char(10), base-62 key.
   * 
   * @param key the 10 character key
   * @param kd the key detail
   */
  public native void decomposeKey(String key, KeyDetail kd);

}
