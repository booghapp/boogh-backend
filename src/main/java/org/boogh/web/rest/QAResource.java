package org.boogh.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.QA;
import org.boogh.repository.QARepository;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing QA.
 */
@RestController
@RequestMapping("/api")
public class QAResource {

    private final Logger log = LoggerFactory.getLogger(QAResource.class);

    private static final String ENTITY_NAME = "qA";

    private final QARepository qARepository;

    public QAResource(QARepository qARepository) {
        this.qARepository = qARepository;
    }

    /**
     * POST  /qas : Create a new qA.
     *
     * @param qA the qA to create
     * @return the ResponseEntity with status 201 (Created) and with body the new qA, or with status 400 (Bad Request) if the qA has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/qas")
    public ResponseEntity<QA> createQA(@Valid @RequestBody QA qA) throws URISyntaxException {
        log.debug("REST request to save QA : {}", qA);
        if (qA.getId() != null) {
            throw new BadRequestAlertException("A new qA cannot already have an ID", ENTITY_NAME, "idexists");
        }
        satisfyOrderConstraint(qA);
        QA result = qARepository.save(qA);
        return ResponseEntity.created(new URI("/api/qas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /qas : Updates an existing qA.
     *
     * @param qA the qA to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated qA,
     * or with status 400 (Bad Request) if the qA is not valid,
     * or with status 500 (Internal Server Error) if the qA couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/qas")
    public ResponseEntity<QA> updateQA(@Valid @RequestBody QA qA) throws URISyntaxException {
        log.debug("REST request to update QA : {}", qA);
        if (qA.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        satisfyOrderConstraint(qA);
        QA result = qARepository.save(qA);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, qA.getId().toString()))
            .body(result);
    }

    /**
     * GET  /qas : get all the qAS.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of qAS in body
     */
    @GetMapping("/qas")
    public List<QA> getAllQAS() {
        log.debug("REST request to get all QAS");
        return qARepository.findAll();
    }

    /**
     * GET  /qas/:id : get the "id" qA.
     *
     * @param id the id of the qA to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the qA, or with status 404 (Not Found)
     */
    @GetMapping("/qas/{id}")
    public ResponseEntity<QA> getQA(@PathVariable Long id) {
        log.debug("REST request to get QA : {}", id);
        Optional<QA> qA = qARepository.findById(id);
        return ResponseUtil.wrapOrNotFound(qA);
    }

    /**
     * DELETE  /qas/:id : delete the "id" qA.
     *
     * @param id the id of the qA to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/qas/{id}")
    public ResponseEntity<Void> deleteQA(@PathVariable Long id) {
        log.debug("REST request to delete QA : {}", id);
        qARepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    private void satisfyOrderConstraint(QA qA){
        //Check if there exist a question with this order
        List<QA> qas = qARepository.findQAByOrder(qA.getOrder());
        if (qas.size() != 0) {
            //Find the maximum order
            Integer maxOrder = qARepository.findMaxOrderQA();
            QA prevQa = qas.get(0);
            prevQa.setOrder(maxOrder + 1);
            qARepository.save(prevQa);
        }
    }
}
