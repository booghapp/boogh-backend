package org.boogh.web.rest;

import org.boogh.BooghApp;
import org.boogh.domain.QA;
import org.boogh.repository.QARepository;
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
 * Test class for the QAResource REST controller.
 *
 * @see QAResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class QAResourceIntTest {

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    @Autowired
    private QARepository qARepository;

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

    private MockMvc restQAMockMvc;

    private QA qA;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QAResource qAResource = new QAResource(qARepository);
        this.restQAMockMvc = MockMvcBuilders.standaloneSetup(qAResource)
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
    public static QA createEntity(EntityManager em) {
        QA qA = new QA()
            .question(DEFAULT_QUESTION)
            .answer(DEFAULT_ANSWER)
            .order(DEFAULT_ORDER);
        return qA;
    }

    @Before
    public void initTest() {
        qA = createEntity(em);
    }

    @Test
    @Transactional
    public void createQA() throws Exception {
        int databaseSizeBeforeCreate = qARepository.findAll().size();

        // Create the QA
        restQAMockMvc.perform(post("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(qA)))
            .andExpect(status().isCreated());

        // Validate the QA in the database
        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeCreate + 1);
        QA testQA = qAList.get(qAList.size() - 1);
        assertThat(testQA.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testQA.getAnswer()).isEqualTo(DEFAULT_ANSWER);
        assertThat(testQA.getOrder()).isEqualTo(DEFAULT_ORDER);
    }

    @Test
    @Transactional
    public void createQAWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = qARepository.findAll().size();

        // Create the QA with an existing ID
        qA.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQAMockMvc.perform(post("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(qA)))
            .andExpect(status().isBadRequest());

        // Validate the QA in the database
        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkQuestionIsRequired() throws Exception {
        int databaseSizeBeforeTest = qARepository.findAll().size();
        // set the field null
        qA.setQuestion(null);

        // Create the QA, which fails.

        restQAMockMvc.perform(post("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(qA)))
            .andExpect(status().isBadRequest());

        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAnswerIsRequired() throws Exception {
        int databaseSizeBeforeTest = qARepository.findAll().size();
        // set the field null
        qA.setAnswer(null);

        // Create the QA, which fails.

        restQAMockMvc.perform(post("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(qA)))
            .andExpect(status().isBadRequest());

        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = qARepository.findAll().size();
        // set the field null
        qA.setOrder(null);

        // Create the QA, which fails.

        restQAMockMvc.perform(post("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(qA)))
            .andExpect(status().isBadRequest());

        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQAS() throws Exception {
        // Initialize the database
        qARepository.saveAndFlush(qA);

        // Get all the qAList
        restQAMockMvc.perform(get("/api/qas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(qA.getId().intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER.toString())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }
    
    @Test
    @Transactional
    public void getQA() throws Exception {
        // Initialize the database
        qARepository.saveAndFlush(qA);

        // Get the qA
        restQAMockMvc.perform(get("/api/qas/{id}", qA.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(qA.getId().intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION.toString()))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER.toString()))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    public void getNonExistingQA() throws Exception {
        // Get the qA
        restQAMockMvc.perform(get("/api/qas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQA() throws Exception {
        // Initialize the database
        qARepository.saveAndFlush(qA);

        int databaseSizeBeforeUpdate = qARepository.findAll().size();

        // Update the qA
        QA updatedQA = qARepository.findById(qA.getId()).get();
        // Disconnect from session so that the updates on updatedQA are not directly saved in db
        em.detach(updatedQA);
        updatedQA
            .question(UPDATED_QUESTION)
            .answer(UPDATED_ANSWER)
            .order(UPDATED_ORDER);

        restQAMockMvc.perform(put("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQA)))
            .andExpect(status().isOk());

        // Validate the QA in the database
        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeUpdate);
        QA testQA = qAList.get(qAList.size() - 1);
        assertThat(testQA.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testQA.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testQA.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    public void updateNonExistingQA() throws Exception {
        int databaseSizeBeforeUpdate = qARepository.findAll().size();

        // Create the QA

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQAMockMvc.perform(put("/api/qas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(qA)))
            .andExpect(status().isBadRequest());

        // Validate the QA in the database
        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteQA() throws Exception {
        // Initialize the database
        qARepository.saveAndFlush(qA);

        int databaseSizeBeforeDelete = qARepository.findAll().size();

        // Delete the qA
        restQAMockMvc.perform(delete("/api/qas/{id}", qA.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<QA> qAList = qARepository.findAll();
        assertThat(qAList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QA.class);
        QA qA1 = new QA();
        qA1.setId(1L);
        QA qA2 = new QA();
        qA2.setId(qA1.getId());
        assertThat(qA1).isEqualTo(qA2);
        qA2.setId(2L);
        assertThat(qA1).isNotEqualTo(qA2);
        qA1.setId(null);
        assertThat(qA1).isNotEqualTo(qA2);
    }
}
