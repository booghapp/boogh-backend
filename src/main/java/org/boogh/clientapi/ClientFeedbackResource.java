package org.boogh.clientapi;

import org.boogh.clientservice.HtmlEscaper;
import org.boogh.config.Constants;
import org.boogh.domain.Feedback;
import org.boogh.repository.FeedbackRepository;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.errors.FeedbackContentMissingException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

/**
 * REST controller for managing Feedback.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientFeedbackResource {

    private final Logger log = LoggerFactory.getLogger(org.boogh.web.rest.FeedbackResource.class);

    private static final String ENTITY_NAME = "feedback";

    private final FeedbackRepository feedbackRepository;

    public ClientFeedbackResource(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * POST  /feedbacks : Create a new feedback.
     *
     * @param feedback the feedback to create
     * @return the ResponseEntity with status 201 (Created) and with body the new feedback, or with status 400 (Bad Request) if the feedback has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/feedbacks")
    public ResponseEntity<Feedback> createFeedback(@Valid @RequestBody Feedback feedback) throws URISyntaxException {
        log.debug("REST request to save Feedback : {}", feedback);
        if (feedback.getId() != null) {
            throw new BadRequestAlertException("A new feedback cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (feedback.getContent() == null || feedback.getContent().length() < 1) {
            throw new FeedbackContentMissingException();
        }

        feedback.setCreatedOn(LocalDate.now());
        HtmlEscaper.escapeFeedback(feedback);
        Feedback result = feedbackRepository.save(feedback);
        return ResponseEntity.created(new URI("/api/feedbacks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
