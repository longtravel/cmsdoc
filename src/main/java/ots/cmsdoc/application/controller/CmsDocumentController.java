package ots.cmsdoc.application.controller;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ots.cmsdoc.application.dms.Dms;
import ots.cmsdoc.application.dms.LogicalDocDMS;
import ots.cmsdoc.application.util.CmsProperties;
import ots.cmsdoc.application.models.CmsDocument;
import ots.cmsdoc.application.repositories.CmsDocumentRepository;
import ots.cmsdoc.application.util.Compression;

/*
(RMT-9/5/2019) Service for creating a file
 */
@RestController
@RequestMapping("api")
public class CmsDocumentController {

  @Autowired
  private CmsProperties props;
  @Autowired
  private Dms dms;
  private final CmsDocumentRepository cmsRepo;

  @Autowired
  public CmsDocumentController(CmsDocumentRepository cmsRepo) {
    this.cmsRepo = cmsRepo;
  }

//TODO - break this into two parts: the service that can be integration testted, and the method for saving a file
    //@ApiOperation(value = "Retrieve a CMS doc and save to configurable storage")
    @RequestMapping(value = "/file", method = POST)
    public boolean saveFile(String locator) {
      CmsDocument cmsDoc;
      Optional<CmsDocument> cmsDocOpt = cmsRepo.findById(locator);
      if (cmsDocOpt.isPresent()) {
        cmsDoc = cmsDocOpt.get();
        Compression compression = new Compression(cmsDoc);  // get the base doc
        String filePath =  props.getFileStorage() + "/" + cmsDoc.getDocName();
        Path locFile = Paths.get(trim(filePath));
        if(compression.writeFile(locFile)) {
          return (saveToDms(filePath, cmsDoc));
        } else {
          System.out.println("temp file write was unsuccessful");
        }
      } else {
        System.out.println("no value found");
      }
      return false;
    }

    //TODO - add test to mock DMS and return true
    private boolean saveToDms (String file, CmsDocument doc) {
      HashMap<String, String> metadata = new HashMap<String, String>();
      metadata.put("docName", doc.getDocName());
      metadata.put("docLocator", doc.getId());
      //TODO - add exception handling
      return(dms.save(file,metadata));
    }
}
