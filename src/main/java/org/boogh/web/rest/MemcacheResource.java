package org.boogh.web.rest;
import org.boogh.domain.Memcache;
import org.boogh.repository.MemcacheRepository;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
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
 * REST controller for managing Memcache.
 */
@RestController
@RequestMapping("/api")
public class MemcacheResource {

    private final Logger log = LoggerFactory.getLogger(MemcacheResource.class);

    private static final String ENTITY_NAME = "memcache";

    private final MemcacheRepository memcacheRepository;

    public MemcacheResource(MemcacheRepository memcacheRepository) {
        this.memcacheRepository = memcacheRepository;
    }

    /**
     * POST  /memcaches : Create a new memcache.
     *
     * @param memcache the memcache to create
     * @return the ResponseEntity with status 201 (Created) and with body the new memcache, or with status 400 (Bad Request) if the memcache has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/memcaches")
    public ResponseEntity<Memcache> createMemcache(@Valid @RequestBody Memcache memcache) throws URISyntaxException {
        log.debug("REST request to save Memcache : {}", memcache);
        if (memcache.getId() != null) {
            throw new BadRequestAlertException("A new memcache cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Memcache result = memcacheRepository.save(memcache);
        return ResponseEntity.created(new URI("/api/memcaches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /memcaches : Updates an existing memcache.
     *
     * @param memcache the memcache to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated memcache,
     * or with status 400 (Bad Request) if the memcache is not valid,
     * or with status 500 (Internal Server Error) if the memcache couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/memcaches")
    public ResponseEntity<Memcache> updateMemcache(@Valid @RequestBody Memcache memcache) throws URISyntaxException {
        log.debug("REST request to update Memcache : {}", memcache);
        if (memcache.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Memcache result = memcacheRepository.save(memcache);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, memcache.getId().toString()))
            .body(result);
    }

    /**
     * GET  /memcaches : get all the memcaches.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of memcaches in body
     */
    @GetMapping("/memcaches")
    public List<Memcache> getAllMemcaches() {
        log.debug("REST request to get all Memcaches");
        return memcacheRepository.findAll();
    }

    /**
     * GET  /memcaches/:id : get the "id" memcache.
     *
     * @param id the id of the memcache to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the memcache, or with status 404 (Not Found)
     */
    @GetMapping("/memcaches/{id}")
    public ResponseEntity<Memcache> getMemcache(@PathVariable Long id) {
        log.debug("REST request to get Memcache : {}", id);
        Optional<Memcache> memcache = memcacheRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(memcache);
    }

    /**
     * DELETE  /memcaches/:id : delete the "id" memcache.
     *
     * @param id the id of the memcache to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/memcaches/{id}")
    public ResponseEntity<Void> deleteMemcache(@PathVariable Long id) {
        log.debug("REST request to delete Memcache : {}", id);
        memcacheRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
