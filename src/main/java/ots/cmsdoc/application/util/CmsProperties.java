package ots.cmsdoc.application.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("cms")
public class CmsProperties {

  private String docApi;
  private String cwsDocExtractAPI;
  private String fileStorage;
  private Long dmsTemplateId;
  private Long dmsFolderId;
  private String jdbcUrl;
  private String db2User;
  private String db2Pass;
  private String dmsUser;
  private String dmsPass;

  public String getDmsUser() {
    return dmsUser;
  }
  public void setDmsUser(String dmsUser) {
    this.dmsUser = dmsUser;
  }
  public String getDmsPass() {
    return dmsPass;
  }
  public void setDmsPass(String dmsPass) {
    this.dmsPass = dmsPass;
  }

  public String getDb2User() {return db2User;}
  public void setDb2User(String db2User){this.db2User = db2User;}

  public String getDb2Pass() {return db2Pass;}

  public void setDb2Pass(String db2Pass) {
    this.db2Pass = db2Pass;
  }

  public String getJdbcUrl() {return jdbcUrl;}

  public void setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
  }

  public String getDocApi() {
    return docApi;
  }

  public void setDocApi(String docApi) {
    this.docApi = docApi;
  }

  public String getCwsDocExtractAPI() {
    return cwsDocExtractAPI;
  }
  public void setCwsDocExtractAPI(String cwsDocExtractAPI) {
    this.cwsDocExtractAPI = cwsDocExtractAPI;
  }

  public String getFileStorage() {
    return this.fileStorage;
  }

  public void setFileStorage(String cmsFileStorage) {
    this.fileStorage = cmsFileStorage;
  }

  public Long getDmsTemplateId () { return this.dmsTemplateId; }

  public void setDmsTemplateId (Long dmsTemplateId ) { this.dmsTemplateId = dmsTemplateId; }

  public Long getDmsFolderId () { return this.dmsFolderId; }

  public void setDmsFolderId (Long dmsFolderId ) { this.dmsFolderId = dmsFolderId; }
}

