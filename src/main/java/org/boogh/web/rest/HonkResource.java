package org.boogh.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.Honk;
import org.boogh.repository.HonkRepository;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import org.boogh.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Honk.
 */
@RestController
@RequestMapping("/api")
public class HonkResource {

    private final Logger log = LoggerFactory.getLogger(HonkResource.class);

    private static final String ENTITY_NAME = "honk";

    private final HonkRepository honkRepository;

    public HonkResource(HonkRepository honkRepository) {
        this.honkRepository = honkRepository;
    }

    /**
     * POST  /honks : Create a new honk.
     *
     * @param honk the honk to create
     * @return the ResponseEntity with status 201 (Created) and with body the new honk, or with status 400 (Bad Request) if the honk has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/honks")
    public ResponseEntity<Honk> createHonk(@Valid @RequestBody Honk honk) throws URISyntaxException {
        log.debug("REST request to save Honk : {}", honk);
        if (honk.getId() != null) {
            throw new BadRequestAlertException("A new honk cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Honk result = honkRepository.save(honk);
        return ResponseEntity.created(new URI("/api/honks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /honks : Updates an existing honk.
     *
     * @param honk the honk to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated honk,
     * or with status 400 (Bad Request) if the honk is not valid,
     * or with status 500 (Internal Server Error) if the honk couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/honks")
    public ResponseEntity<Honk> updateHonk(@Valid @RequestBody Honk honk) throws URISyntaxException {
        log.debug("REST request to update Honk : {}", honk);
        if (honk.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Honk result = honkRepository.save(honk);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, honk.getId().toString()))
            .body(result);
    }

    /**
     * GET  /honks : get all the honks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of honks in body
     */
    @GetMapping("/honks")
    public ResponseEntity<List<Honk>> getAllHonks(Pageable pageable) {
        log.debug("REST request to get a page of Honks");
        Page<Honk> page = honkRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/honks");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /honks/:id : get the "id" honk.
     *
     * @param id the id of the honk to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the honk, or with status 404 (Not Found)
     */
    @GetMapping("/honks/{id}")
    public ResponseEntity<Honk> getHonk(@PathVariable Long id) {
        log.debug("REST request to get Honk : {}", id);
        Optional<Honk> honk = honkRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(honk);
    }

    /**
     * DELETE  /honks/:id : delete the "id" honk.
     *
     * @param id the id of the honk to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/honks/{id}")
    public ResponseEntity<Void> deleteHonk(@PathVariable Long id) {
        log.debug("REST request to delete Honk : {}", id);
        honkRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
