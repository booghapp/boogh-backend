package org.boogh.clientapi;

import org.boogh.clientservice.CheckAuth;
import org.boogh.clientservice.dto.HonkDTO;
import org.boogh.config.Constants;
import org.boogh.domain.Honk;
import org.boogh.repository.HonkRepository;
import org.boogh.web.rest.HonkResource;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing Honk.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientHonkResource {

    private final Logger log = LoggerFactory.getLogger(HonkResource.class);

    private static final String ENTITY_NAME = "honk";

    private final HonkRepository honkRepository;

    private final CheckAuth checkAuth;

    public ClientHonkResource(HonkRepository honkRepository, CheckAuth checkAuth) {
        this.honkRepository = honkRepository;
        this.checkAuth = checkAuth;
    }

    /**
     * POST  /honks : Create a new honk.
     *
     * @param honk the honk to create
     * @return the ResponseEntity with status 201 (Created) and with body the new honk, or with status 400 (Bad Request) if the honk has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/honks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HonkDTO> createHonk(@Valid @RequestBody Honk honk) throws URISyntaxException {
        log.debug("REST request to save Honk : {}", honk);
        if (honk.getId() != null) {
            throw new BadRequestAlertException("A new honk cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (!checkAuth.hasAuthority(honk.getUser().getId())) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        // Check if the user has honked for this report before
        List<Honk> previousHonk = honkRepository.findUserHonkForReport(honk.getUser().getId(), honk.getReport().getId());
        Honk result;
        if (previousHonk.size() != 0) {
            Honk prevHonk = previousHonk.get(0);
            prevHonk.setHonked(honk.isHonked());
            result = honkRepository.save(prevHonk);
        } else {
            result = honkRepository.save(honk);
        }

        HonkDTO honkDTO = new HonkDTO(result);
        return ResponseEntity.created(new URI("/api/honks/" + honkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, honkDTO.getId().toString()))
            .body(honkDTO);
    }

    /**
     * Get number of honks for report
     *
     * @param reportId the id of the report
     * @return the ResponseEntity with status 200 (OK) and with body of Integer, or with status 404 (Not Found)
     */
    @GetMapping(value = "/honks", params = {"reportId"})
    public ResponseEntity<Integer> getNumHonksForReport(@RequestParam Long reportId) {
        log.debug("REST request to get number of honks for report id : {}", reportId);
        Integer numHonks = honkRepository.findNumHonksForReport(reportId);
        return ResponseEntity.ok().body(numHonks);
    }

    /**
     * Get user honk for report
     *
     * @param userId the id of the user
     * @param reportId the id of the report
     * @return the ResponseEntity with status 200 (OK) and with body of boolean, or with status 404 (Not Found)
     */
    @GetMapping(value = "/honks", params = {"userId", "reportId"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> getUserHonkForReport(@RequestParam Long userId, @RequestParam Long reportId) {
        log.debug("REST request to get user honk for report");

        if (!checkAuth.hasAuthority(userId)) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        List<Honk> honk = honkRepository.findUserHonkForReport(userId, reportId);
        boolean hasUserHonked = false;
        if (honk.size() != 0) {
            hasUserHonked = honk.get(0).isHonked();
        }
        return ResponseEntity.ok().body(hasUserHonked);
    }
}
