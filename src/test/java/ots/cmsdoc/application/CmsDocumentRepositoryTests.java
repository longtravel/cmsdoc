package ots.cmsdoc.application;

import java.util.Optional;
import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ots.cmsdoc.application.models.CmsDocument;
import ots.cmsdoc.application.repositories.CmsDocumentRepository;
import org.assertj.core.api.Assertions.*;

//@SpringBootTest
@ContextConfiguration(classes=Application.class)
//@RunWith(SpringRunner.class)
@DataJpaTest
public class CmsDocumentRepositoryTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private CmsDocumentRepository cmsDocumentRepository;

	@Test
	public void whenGetId() {
		// when
		CmsDocument localCms = null;
		Optional<CmsDocument> found = cmsDocumentRepository.findById("0000351601020120*ERSUPND 00004");
		if (found.isPresent()) localCms = found.get();

		// then
		assert(localCms.getDocName()).equals("icpc1.doc");
	}

}
