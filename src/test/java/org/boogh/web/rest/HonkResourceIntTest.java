package org.boogh.web.rest;

import org.boogh.BooghApp;
import org.boogh.domain.Honk;
import org.boogh.domain.Report;
import org.boogh.domain.User;
import org.boogh.repository.HonkRepository;
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
 * Test class for the HonkResource REST controller.
 *
 * @see HonkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class HonkResourceIntTest {

    private static final Boolean DEFAULT_HONKED = false;
    private static final Boolean UPDATED_HONKED = true;

    @Autowired
    private HonkRepository honkRepository;

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

    private MockMvc restHonkMockMvc;

    private Honk honk;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final HonkResource honkResource = new HonkResource(honkRepository);
        this.restHonkMockMvc = MockMvcBuilders.standaloneSetup(honkResource)
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
    public static Honk createEntity(EntityManager em) {
        Honk honk = new Honk()
            .honked(DEFAULT_HONKED);
        // Add required entity
        Report report = ReportResourceIntTest.createEntity(em);
        em.persist(report);
        em.flush();
        honk.setReport(report);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        honk.setUser(user);
        return honk;
    }

    @Before
    public void initTest() {
        honk = createEntity(em);
    }

    @Test
    @Transactional
    public void createHonk() throws Exception {
        int databaseSizeBeforeCreate = honkRepository.findAll().size();

        // Create the Honk
        restHonkMockMvc.perform(post("/api/honks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(honk)))
            .andExpect(status().isCreated());

        // Validate the Honk in the database
        List<Honk> honkList = honkRepository.findAll();
        assertThat(honkList).hasSize(databaseSizeBeforeCreate + 1);
        Honk testHonk = honkList.get(honkList.size() - 1);
        assertThat(testHonk.isHonked()).isEqualTo(DEFAULT_HONKED);
    }

    @Test
    @Transactional
    public void createHonkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = honkRepository.findAll().size();

        // Create the Honk with an existing ID
        honk.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restHonkMockMvc.perform(post("/api/honks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(honk)))
            .andExpect(status().isBadRequest());

        // Validate the Honk in the database
        List<Honk> honkList = honkRepository.findAll();
        assertThat(honkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkHonkedIsRequired() throws Exception {
        int databaseSizeBeforeTest = honkRepository.findAll().size();
        // set the field null
        honk.setHonked(null);

        // Create the Honk, which fails.

        restHonkMockMvc.perform(post("/api/honks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(honk)))
            .andExpect(status().isBadRequest());

        List<Honk> honkList = honkRepository.findAll();
        assertThat(honkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHonks() throws Exception {
        // Initialize the database
        honkRepository.saveAndFlush(honk);

        // Get all the honkList
        restHonkMockMvc.perform(get("/api/honks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(honk.getId().intValue())))
            .andExpect(jsonPath("$.[*].honked").value(hasItem(DEFAULT_HONKED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getHonk() throws Exception {
        // Initialize the database
        honkRepository.saveAndFlush(honk);

        // Get the honk
        restHonkMockMvc.perform(get("/api/honks/{id}", honk.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(honk.getId().intValue()))
            .andExpect(jsonPath("$.honked").value(DEFAULT_HONKED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingHonk() throws Exception {
        // Get the honk
        restHonkMockMvc.perform(get("/api/honks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHonk() throws Exception {
        // Initialize the database
        honkRepository.saveAndFlush(honk);

        int databaseSizeBeforeUpdate = honkRepository.findAll().size();

        // Update the honk
        Honk updatedHonk = honkRepository.findById(honk.getId()).get();
        // Disconnect from session so that the updates on updatedHonk are not directly saved in db
        em.detach(updatedHonk);
        updatedHonk
            .honked(UPDATED_HONKED);

        restHonkMockMvc.perform(put("/api/honks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedHonk)))
            .andExpect(status().isOk());

        // Validate the Honk in the database
        List<Honk> honkList = honkRepository.findAll();
        assertThat(honkList).hasSize(databaseSizeBeforeUpdate);
        Honk testHonk = honkList.get(honkList.size() - 1);
        assertThat(testHonk.isHonked()).isEqualTo(UPDATED_HONKED);
    }

    @Test
    @Transactional
    public void updateNonExistingHonk() throws Exception {
        int databaseSizeBeforeUpdate = honkRepository.findAll().size();

        // Create the Honk

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHonkMockMvc.perform(put("/api/honks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(honk)))
            .andExpect(status().isBadRequest());

        // Validate the Honk in the database
        List<Honk> honkList = honkRepository.findAll();
        assertThat(honkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteHonk() throws Exception {
        // Initialize the database
        honkRepository.saveAndFlush(honk);

        int databaseSizeBeforeDelete = honkRepository.findAll().size();

        // Delete the honk
        restHonkMockMvc.perform(delete("/api/honks/{id}", honk.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Honk> honkList = honkRepository.findAll();
        assertThat(honkList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Honk.class);
        Honk honk1 = new Honk();
        honk1.setId(1L);
        Honk honk2 = new Honk();
        honk2.setId(honk1.getId());
        assertThat(honk1).isEqualTo(honk2);
        honk2.setId(2L);
        assertThat(honk1).isNotEqualTo(honk2);
        honk1.setId(null);
        assertThat(honk1).isNotEqualTo(honk2);
    }
}
