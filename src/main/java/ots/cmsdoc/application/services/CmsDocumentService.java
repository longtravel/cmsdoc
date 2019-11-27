package ots.cmsdoc.application.services;

import ots.cmsdoc.application.repositories.CmsDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsDocumentService {

    @Autowired
    private CmsDocumentRepository cmsDocumentRepository;

    public List<ots.cmsdoc.application.models.CmsDocument> list() {
        //TODO: change to find one?
        return cmsDocumentRepository.findAll();
    }
}
