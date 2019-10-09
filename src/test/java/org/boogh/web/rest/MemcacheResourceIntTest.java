package org.boogh.web.rest;

import org.boogh.BooghApp;

import org.boogh.domain.Memcache;
import org.boogh.domain.User;
import org.boogh.repository.MemcacheRepository;
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
 * Test class for the MemcacheResource REST controller.
 *
 * @see MemcacheResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class MemcacheResourceIntTest {

    private static final String DEFAULT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_HASH = "BBBBBBBBBB";

    private static final Long DEFAULT_TELEGRAM_ID = 1L;
    private static final Long UPDATED_TELEGRAM_ID = 2L;

    @Autowired
    private MemcacheRepository memcacheRepository;

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

    private MockMvc restMemcacheMockMvc;

    private Memcache memcache;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MemcacheResource memcacheResource = new MemcacheResource(memcacheRepository);
        this.restMemcacheMockMvc = MockMvcBuilders.standaloneSetup(memcacheResource)
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
    public static Memcache createEntity(EntityManager em) {
        Memcache memcache = new Memcache()
            .hash(DEFAULT_HASH)
            .telegramId(DEFAULT_TELEGRAM_ID);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        memcache.setUser(user);
        return memcache;
    }

    @Before
    public void initTest() {
        memcache = createEntity(em);
    }

    @Test
    @Transactional
    public void createMemcache() throws Exception {
        int databaseSizeBeforeCreate = memcacheRepository.findAll().size();

        // Create the Memcache
        restMemcacheMockMvc.perform(post("/api/memcaches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memcache)))
            .andExpect(status().isCreated());

        // Validate the Memcache in the database
        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeCreate + 1);
        Memcache testMemcache = memcacheList.get(memcacheList.size() - 1);
        assertThat(testMemcache.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testMemcache.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
    }

    @Test
    @Transactional
    public void createMemcacheWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = memcacheRepository.findAll().size();

        // Create the Memcache with an existing ID
        memcache.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMemcacheMockMvc.perform(post("/api/memcaches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memcache)))
            .andExpect(status().isBadRequest());

        // Validate the Memcache in the database
        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkHashIsRequired() throws Exception {
        int databaseSizeBeforeTest = memcacheRepository.findAll().size();
        // set the field null
        memcache.setHash(null);

        // Create the Memcache, which fails.

        restMemcacheMockMvc.perform(post("/api/memcaches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memcache)))
            .andExpect(status().isBadRequest());

        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelegramIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = memcacheRepository.findAll().size();
        // set the field null
        memcache.setTelegramId(null);

        // Create the Memcache, which fails.

        restMemcacheMockMvc.perform(post("/api/memcaches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memcache)))
            .andExpect(status().isBadRequest());

        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMemcaches() throws Exception {
        // Initialize the database
        memcacheRepository.saveAndFlush(memcache);

        // Get all the memcacheList
        restMemcacheMockMvc.perform(get("/api/memcaches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(memcache.getId().intValue())))
            .andExpect(jsonPath("$.[*].hash").value(hasItem(DEFAULT_HASH.toString())))
            .andExpect(jsonPath("$.[*].telegramId").value(hasItem(DEFAULT_TELEGRAM_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getMemcache() throws Exception {
        // Initialize the database
        memcacheRepository.saveAndFlush(memcache);

        // Get the memcache
        restMemcacheMockMvc.perform(get("/api/memcaches/{id}", memcache.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(memcache.getId().intValue()))
            .andExpect(jsonPath("$.hash").value(DEFAULT_HASH.toString()))
            .andExpect(jsonPath("$.telegramId").value(DEFAULT_TELEGRAM_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMemcache() throws Exception {
        // Get the memcache
        restMemcacheMockMvc.perform(get("/api/memcaches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMemcache() throws Exception {
        // Initialize the database
        memcacheRepository.saveAndFlush(memcache);

        int databaseSizeBeforeUpdate = memcacheRepository.findAll().size();

        // Update the memcache
        Memcache updatedMemcache = memcacheRepository.findById(memcache.getId()).get();
        // Disconnect from session so that the updates on updatedMemcache are not directly saved in db
        em.detach(updatedMemcache);
        updatedMemcache
            .hash(UPDATED_HASH)
            .telegramId(UPDATED_TELEGRAM_ID);

        restMemcacheMockMvc.perform(put("/api/memcaches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMemcache)))
            .andExpect(status().isOk());

        // Validate the Memcache in the database
        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeUpdate);
        Memcache testMemcache = memcacheList.get(memcacheList.size() - 1);
        assertThat(testMemcache.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testMemcache.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingMemcache() throws Exception {
        int databaseSizeBeforeUpdate = memcacheRepository.findAll().size();

        // Create the Memcache

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemcacheMockMvc.perform(put("/api/memcaches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memcache)))
            .andExpect(status().isBadRequest());

        // Validate the Memcache in the database
        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMemcache() throws Exception {
        // Initialize the database
        memcacheRepository.saveAndFlush(memcache);

        int databaseSizeBeforeDelete = memcacheRepository.findAll().size();

        // Delete the memcache
        restMemcacheMockMvc.perform(delete("/api/memcaches/{id}", memcache.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Memcache> memcacheList = memcacheRepository.findAll();
        assertThat(memcacheList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Memcache.class);
        Memcache memcache1 = new Memcache();
        memcache1.setId(1L);
        Memcache memcache2 = new Memcache();
        memcache2.setId(memcache1.getId());
        assertThat(memcache1).isEqualTo(memcache2);
        memcache2.setId(2L);
        assertThat(memcache1).isNotEqualTo(memcache2);
        memcache1.setId(null);
        assertThat(memcache1).isNotEqualTo(memcache2);
    }
}
