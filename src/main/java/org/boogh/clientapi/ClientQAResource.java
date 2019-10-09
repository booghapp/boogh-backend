package org.boogh.clientapi;

import org.boogh.config.Constants;
import org.boogh.domain.QA;
import org.boogh.repository.QARepository;
import org.boogh.web.rest.QAResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST controller for managing QA.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientQAResource {

    private final Logger log = LoggerFactory.getLogger(QAResource.class);

    private static final String ENTITY_NAME = "qA";

    private final QARepository qARepository;

    public ClientQAResource(QARepository qARepository) {
        this.qARepository = qARepository;
    }

    /**
     * GET  /qas : get all the qAS.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of qAS in body
     */
    @GetMapping("/qas")
    public List<QA> getAllQAS() {
        log.debug("REST request to get all QAS by order asc");
        return qARepository.findAllByOrderAsc();
    }
}
