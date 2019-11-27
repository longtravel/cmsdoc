package ots.cmsdoc.application.repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CmsDocumentRepository extends JpaRepository<ots.cmsdoc.application.models.CmsDocument, String> {
}
