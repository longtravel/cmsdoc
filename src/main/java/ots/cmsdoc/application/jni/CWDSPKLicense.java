package ots.cmsdoc.application.jni;

/**
 * Not currently in use.
 * 
 * <p>
 * Stores the PKWare license key, if needed, for future use.
 * </p>
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface CWDSPKLicense {

  /**
   * License key required for proprietary PKWare algorithms.
   * 
   * @return the base64 license key
   */
  String getLicenseKey();

}
