package org.boogh.clientapi;

import org.boogh.config.Constants;
import org.boogh.domain.Document;
import org.boogh.domain.enumeration.DocType;
import org.boogh.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing Document.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientDocumentResource {

    private final Logger log = LoggerFactory.getLogger(ClientDocumentResource.class);

    private static final String ENTITY_NAME = "document";

    private final DocumentRepository documentRepository;

    public ClientDocumentResource(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * GET  /documents/:type: get the type document.
     *
     * @param type the type of the document to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the document, or with status 404 (Not Found)
     */
    @GetMapping("/documents/{type}")
    public List<Document> getDocumentByType(@PathVariable DocType type) {
        log.debug("REST request to get Document of type: {}", type);
        List<Document> document = documentRepository.findDocumentByType(type);
        return document;
    }
}
