package ots.cmsdoc.application.dms;

import com.logicaldoc.webservice.model.WSAttribute;
import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.rest.client.RestDocumentClient;
import java.io.File;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ots.cmsdoc.application.util.CmsProperties;


@Component
public class LogicalDocDMS implements Dms {
  private CmsProperties props;

  @Autowired
  public LogicalDocDMS(CmsProperties props) {
    this.props=props;
  }

  private void DmsLogicalImpl() {
    System.out.println("Initiating DMS LogicalDoc");
  }

  public boolean save(String filename, HashMap map) {
    // String restEndpt = props.getDocApi();
    RestDocumentClient restClient = new RestDocumentClient( props.getDocApi(), props.getDmsUser(), props.getDmsPass());
    File file = new File(filename);
    //TODO: add this test
    if (!file.exists()) {
      System.out.println("File not found: " + filename.toString());
      throw new RuntimeException();
    }
    // get the filesize in order to check
    long fsize = file.length();
    WSDocument wsdocument = new WSDocument();
    wsdocument.setFolderId(props.getDmsFolderId());// this is required by LogicalDoc
    wsdocument.setFileName(file.getName());
    wsdocument.setTemplateId(props.getDmsTemplateId());
    wsdocument.setAttributes(setLogicalDocMetadata(map));
    WSDocument createdDoc = new WSDocument();
    try {
      createdDoc = restClient.create(wsdocument, file);
      System.out.println("Document created with ID: " + createdDoc.getId());
      return(true);
    } catch (Exception e) {
      System.out.println("DMS Operation failed: " + e.toString());
    }
    return(false);
  }

  private WSAttribute[] setLogicalDocMetadata(HashMap map) {
    WSAttribute[] wsa = new WSAttribute[map.size()];
    // =========== countryCode
    // NOTE: if the attributes are set to "mandatory", not including them will result in a error 500
    // === Doc Locator ======
    WSAttribute attr1 = new WSAttribute();
    attr1.setName("docLocator");
    attr1.setType(0);
    attr1.setStringValue(map.get("docLocator").toString());
    //attr1.setSetId(props.getDmsTemplateId());
    attr1.setValue(map.get("docLocator").toString());
    wsa[0] = attr1;
    // ===========Case ID===============
    WSAttribute attr2 = new WSAttribute();
    attr2.setName("docName");
    attr2.setType(0);
    attr2.setStringValue(map.get("docName").toString());
    //attr1.setSetId(props.getDmsTemplateId());
    attr2.setValue(map.get("docName").toString());
    wsa[1] = attr2;
    return (wsa);
  }
}
