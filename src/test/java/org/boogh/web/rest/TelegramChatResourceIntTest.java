package org.boogh.web.rest;

import org.boogh.BooghApp;
import org.boogh.domain.TelegramChat;
import org.boogh.domain.User;
import org.boogh.repository.TelegramChatRepository;
import org.boogh.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.boogh.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TelegramChatResource REST controller.
 *
 * @see TelegramChatResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class TelegramChatResourceIntTest {

    private static final Long DEFAULT_CHAT_ID = 1L;
    private static final Long UPDATED_CHAT_ID = 2L;

    private static final Long DEFAULT_TELEGRAM_USER_ID = 1L;
    private static final Long UPDATED_TELEGRAM_USER_ID = 2L;

    @Autowired
    private TelegramChatRepository telegramChatRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTelegramChatMockMvc;

    private TelegramChat telegramChat;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TelegramChatResource telegramChatResource = new TelegramChatResource(telegramChatRepository);
        this.restTelegramChatMockMvc = MockMvcBuilders.standaloneSetup(telegramChatResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelegramChat createEntity(EntityManager em) {
        TelegramChat telegramChat = new TelegramChat()
            .chatId(DEFAULT_CHAT_ID)
            .telegramUserId(DEFAULT_TELEGRAM_USER_ID);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        telegramChat.setUser(user);
        return telegramChat;
    }

    @Before
    public void initTest() {
        telegramChat = createEntity(em);
    }

    @Test
    @Transactional
    public void createTelegramChat() throws Exception {
        int databaseSizeBeforeCreate = telegramChatRepository.findAll().size();

        // Create the TelegramChat
        restTelegramChatMockMvc.perform(post("/api/telegram-chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(telegramChat)))
            .andExpect(status().isCreated());

        // Validate the TelegramChat in the database
        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeCreate + 1);
        TelegramChat testTelegramChat = telegramChatList.get(telegramChatList.size() - 1);
        assertThat(testTelegramChat.getChatId()).isEqualTo(DEFAULT_CHAT_ID);
        assertThat(testTelegramChat.getTelegramUserId()).isEqualTo(DEFAULT_TELEGRAM_USER_ID);
    }

    @Test
    @Transactional
    public void createTelegramChatWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = telegramChatRepository.findAll().size();

        // Create the TelegramChat with an existing ID
        telegramChat.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTelegramChatMockMvc.perform(post("/api/telegram-chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(telegramChat)))
            .andExpect(status().isBadRequest());

        // Validate the TelegramChat in the database
        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkChatIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = telegramChatRepository.findAll().size();
        // set the field null
        telegramChat.setChatId(null);

        // Create the TelegramChat, which fails.

        restTelegramChatMockMvc.perform(post("/api/telegram-chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(telegramChat)))
            .andExpect(status().isBadRequest());

        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelegramUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = telegramChatRepository.findAll().size();
        // set the field null
        telegramChat.setTelegramUserId(null);

        // Create the TelegramChat, which fails.

        restTelegramChatMockMvc.perform(post("/api/telegram-chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(telegramChat)))
            .andExpect(status().isBadRequest());

        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTelegramChats() throws Exception {
        // Initialize the database
        telegramChatRepository.saveAndFlush(telegramChat);

        // Get all the telegramChatList
        restTelegramChatMockMvc.perform(get("/api/telegram-chats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(telegramChat.getId().intValue())))
            .andExpect(jsonPath("$.[*].chatId").value(hasItem(DEFAULT_CHAT_ID.intValue())))
            .andExpect(jsonPath("$.[*].telegramUserId").value(hasItem(DEFAULT_TELEGRAM_USER_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getTelegramChat() throws Exception {
        // Initialize the database
        telegramChatRepository.saveAndFlush(telegramChat);

        // Get the telegramChat
        restTelegramChatMockMvc.perform(get("/api/telegram-chats/{id}", telegramChat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(telegramChat.getId().intValue()))
            .andExpect(jsonPath("$.chatId").value(DEFAULT_CHAT_ID.intValue()))
            .andExpect(jsonPath("$.telegramUserId").value(DEFAULT_TELEGRAM_USER_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTelegramChat() throws Exception {
        // Get the telegramChat
        restTelegramChatMockMvc.perform(get("/api/telegram-chats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTelegramChat() throws Exception {
        // Initialize the database
        telegramChatRepository.saveAndFlush(telegramChat);

        int databaseSizeBeforeUpdate = telegramChatRepository.findAll().size();

        // Update the telegramChat
        TelegramChat updatedTelegramChat = telegramChatRepository.findById(telegramChat.getId()).get();
        // Disconnect from session so that the updates on updatedTelegramChat are not directly saved in db
        em.detach(updatedTelegramChat);
        updatedTelegramChat
            .chatId(UPDATED_CHAT_ID)
            .telegramUserId(UPDATED_TELEGRAM_USER_ID);

        restTelegramChatMockMvc.perform(put("/api/telegram-chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTelegramChat)))
            .andExpect(status().isOk());

        // Validate the TelegramChat in the database
        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeUpdate);
        TelegramChat testTelegramChat = telegramChatList.get(telegramChatList.size() - 1);
        assertThat(testTelegramChat.getChatId()).isEqualTo(UPDATED_CHAT_ID);
        assertThat(testTelegramChat.getTelegramUserId()).isEqualTo(UPDATED_TELEGRAM_USER_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingTelegramChat() throws Exception {
        int databaseSizeBeforeUpdate = telegramChatRepository.findAll().size();

        // Create the TelegramChat

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTelegramChatMockMvc.perform(put("/api/telegram-chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(telegramChat)))
            .andExpect(status().isBadRequest());

        // Validate the TelegramChat in the database
        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTelegramChat() throws Exception {
        // Initialize the database
        telegramChatRepository.saveAndFlush(telegramChat);

        int databaseSizeBeforeDelete = telegramChatRepository.findAll().size();

        // Delete the telegramChat
        restTelegramChatMockMvc.perform(delete("/api/telegram-chats/{id}", telegramChat.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TelegramChat> telegramChatList = telegramChatRepository.findAll();
        assertThat(telegramChatList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelegramChat.class);
        TelegramChat telegramChat1 = new TelegramChat();
        telegramChat1.setId(1L);
        TelegramChat telegramChat2 = new TelegramChat();
        telegramChat2.setId(telegramChat1.getId());
        assertThat(telegramChat1).isEqualTo(telegramChat2);
        telegramChat2.setId(2L);
        assertThat(telegramChat1).isNotEqualTo(telegramChat2);
        telegramChat1.setId(null);
        assertThat(telegramChat1).isNotEqualTo(telegramChat2);
    }
}
