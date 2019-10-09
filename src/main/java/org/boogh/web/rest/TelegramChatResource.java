package org.boogh.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.TelegramChat;
import org.boogh.repository.TelegramChatRepository;
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
 * REST controller for managing TelegramChat.
 */
@RestController
@RequestMapping("/api")
public class TelegramChatResource {

    private final Logger log = LoggerFactory.getLogger(TelegramChatResource.class);

    private static final String ENTITY_NAME = "telegramChat";

    private final TelegramChatRepository telegramChatRepository;

    public TelegramChatResource(TelegramChatRepository telegramChatRepository) {
        this.telegramChatRepository = telegramChatRepository;
    }

    /**
     * POST  /telegram-chats : Create a new telegramChat.
     *
     * @param telegramChat the telegramChat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new telegramChat, or with status 400 (Bad Request) if the telegramChat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/telegram-chats")
    public ResponseEntity<TelegramChat> createTelegramChat(@Valid @RequestBody TelegramChat telegramChat) throws URISyntaxException {
        log.debug("REST request to save TelegramChat : {}", telegramChat);
        if (telegramChat.getId() != null) {
            throw new BadRequestAlertException("A new telegramChat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TelegramChat result = telegramChatRepository.save(telegramChat);
        return ResponseEntity.created(new URI("/api/telegram-chats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /telegram-chats : Updates an existing telegramChat.
     *
     * @param telegramChat the telegramChat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated telegramChat,
     * or with status 400 (Bad Request) if the telegramChat is not valid,
     * or with status 500 (Internal Server Error) if the telegramChat couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/telegram-chats")
    public ResponseEntity<TelegramChat> updateTelegramChat(@Valid @RequestBody TelegramChat telegramChat) throws URISyntaxException {
        log.debug("REST request to update TelegramChat : {}", telegramChat);
        if (telegramChat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TelegramChat result = telegramChatRepository.save(telegramChat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, telegramChat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /telegram-chats : get all the telegramChats.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of telegramChats in body
     */
    @GetMapping("/telegram-chats")
    public List<TelegramChat> getAllTelegramChats() {
        log.debug("REST request to get all TelegramChats");
        return telegramChatRepository.findAll();
    }

    /**
     * GET  /telegram-chats/:id : get the "id" telegramChat.
     *
     * @param id the id of the telegramChat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the telegramChat, or with status 404 (Not Found)
     */
    @GetMapping("/telegram-chats/{id}")
    public ResponseEntity<TelegramChat> getTelegramChat(@PathVariable Long id) {
        log.debug("REST request to get TelegramChat : {}", id);
        Optional<TelegramChat> telegramChat = telegramChatRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(telegramChat);
    }

    /**
     * DELETE  /telegram-chats/:id : delete the "id" telegramChat.
     *
     * @param id the id of the telegramChat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/telegram-chats/{id}")
    public ResponseEntity<Void> deleteTelegramChat(@PathVariable Long id) {
        log.debug("REST request to delete TelegramChat : {}", id);
        telegramChatRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
