package org.boogh.web.rest;

import org.boogh.BooghApp;
import org.boogh.domain.Report;
import org.boogh.domain.ReportStatus;
import org.boogh.domain.User;
import org.boogh.domain.enumeration.ReportStatusState;
import org.boogh.repository.ReportStatusRepository;
import org.boogh.service.ReportStatusQueryService;
import org.boogh.service.ReportStatusService;
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
import static org.junit.matchers.JUnitMatchers.*;

import javax.persistence.EntityManager;
import java.util.List;

import static org.boogh.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReportStatusResource REST controller.
 *
 * @see ReportStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class ReportStatusResourceIntTest {

    private static final ReportStatusState DEFAULT_SAVED = ReportStatusState.UNSET;
    private static final ReportStatusState UPDATED_SAVED = ReportStatusState.FALSE;

    private static final ReportStatusState DEFAULT_FLAGGED = ReportStatusState.FALSE;
    private static final ReportStatusState UPDATED_FLAGGED = ReportStatusState.TRUE;

    @Autowired
    private ReportStatusRepository reportStatusRepository;

    @Autowired
    private ReportStatusService reportStatusService;

    @Autowired
    private ReportStatusQueryService reportStatusQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restReportStatusMockMvc;

    private ReportStatus reportStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ReportStatusResource reportStatusResource = new ReportStatusResource(reportStatusService, reportStatusQueryService);
        this.restReportStatusMockMvc = MockMvcBuilders.standaloneSetup(reportStatusResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReportStatus createEntity(EntityManager em) {
        ReportStatus reportStatus = new ReportStatus()
            .saved(DEFAULT_SAVED)
            .flagged(DEFAULT_FLAGGED);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        reportStatus.setReporter(user);
        // Add required entity
        Report report = ReportResourceIntTest.createEntity(em);
        em.persist(report);
        em.flush();
        reportStatus.setReport(report);
        return reportStatus;
    }

    @Before
    public void initTest() {
        reportStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createReportStatus() throws Exception {
        int databaseSizeBeforeCreate = reportStatusRepository.findAll().size();

        // Create the ReportStatus
        restReportStatusMockMvc.perform(post("/api/report-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportStatus)))
            .andExpect(status().isCreated());

        // Validate the ReportStatus in the database
        List<ReportStatus> reportStatusList = reportStatusRepository.findAll();
        assertThat(reportStatusList).hasSize(databaseSizeBeforeCreate + 1);
        ReportStatus testReportStatus = reportStatusList.get(reportStatusList.size() - 1);
        assertThat(testReportStatus.getSaved()).isEqualTo(DEFAULT_SAVED);
        assertThat(testReportStatus.getFlagged()).isEqualTo(DEFAULT_FLAGGED);
    }

    @Test
    @Transactional
    public void createReportStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reportStatusRepository.findAll().size();

        // Create the ReportStatus with an existing ID
        reportStatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportStatusMockMvc.perform(post("/api/report-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportStatus)))
            .andExpect(status().isBadRequest());

        // Validate the ReportStatus in the database
        List<ReportStatus> reportStatusList = reportStatusRepository.findAll();
        assertThat(reportStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createReportStatusWithExistingReporterIdAndReportId() throws Exception {

        ReportStatus reportStatus1 = new ReportStatus()
            .saved(reportStatus.getSaved())
            .flagged(reportStatus.getFlagged());

        reportStatus1.setReport(reportStatus.getReport());
        reportStatus1.setReporter(reportStatus.getReporter());

        reportStatusRepository.saveAndFlush(reportStatus);
        Long id = reportStatus.getId();

        // There can only be one entry for each combination of reporterId + reportId.
        restReportStatusMockMvc.perform(post("/api/report-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportStatus1)))
            .andExpect(status().isCreated());

        //Check if original reportStatus has been updated to the new Saved and Flagged values.
        reportStatus = reportStatusRepository.findById(id).get();
        assertThat(reportStatus1.getSaved()).isEqualTo(reportStatus.getSaved());
        assertThat(reportStatus1.getFlagged()).isEqualTo(reportStatus.getFlagged());
    }

    @Test
    @Transactional
    public void getAllReportStatuses() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList
        restReportStatusMockMvc.perform(get("/api/report-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reportStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].saved").value(hasItem(DEFAULT_SAVED.toString())))
            .andExpect(jsonPath("$.[*].flagged").value(hasItem(DEFAULT_FLAGGED.toString())));
    }
    
    @Test
    @Transactional
    public void getReportStatus() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get the reportStatus
        restReportStatusMockMvc.perform(get("/api/report-statuses/{id}", reportStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(reportStatus.getId().intValue()))
            .andExpect(jsonPath("$.saved").value(DEFAULT_SAVED.toString()))
            .andExpect(jsonPath("$.flagged").value(DEFAULT_FLAGGED.toString()));
    }

    @Test
    @Transactional
    public void getAllReportStatusesBySavedIsEqualToSomething() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList where saved equals to DEFAULT_SAVED
        defaultReportStatusShouldBeFound("saved.equals=" + DEFAULT_SAVED);

        // Get all the reportStatusList where saved equals to UPDATED_SAVED
        defaultReportStatusShouldNotBeFound("saved.equals=" + UPDATED_SAVED);
    }

    @Test
    @Transactional
    public void getAllReportStatusesBySavedIsInShouldWork() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList where saved in DEFAULT_SAVED or UPDATED_SAVED
        defaultReportStatusShouldBeFound("saved.in=" + DEFAULT_SAVED + "," + UPDATED_SAVED);

        // Get all the reportStatusList where saved equals to UPDATED_SAVED
        defaultReportStatusShouldNotBeFound("saved.in=" + UPDATED_SAVED);
    }

    @Test
    @Transactional
    public void getAllReportStatusesBySavedIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList where saved is not null
        defaultReportStatusShouldBeFound("saved.specified=true");

        // Get all the reportStatusList where saved is null
        defaultReportStatusShouldNotBeFound("saved.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportStatusesByFlaggedIsEqualToSomething() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList where flagged equals to DEFAULT_FLAGGED
        defaultReportStatusShouldBeFound("flagged.equals=" + DEFAULT_FLAGGED);

        // Get all the reportStatusList where flagged equals to UPDATED_FLAGGED
        defaultReportStatusShouldNotBeFound("flagged.equals=" + UPDATED_FLAGGED);
    }

    @Test
    @Transactional
    public void getAllReportStatusesByFlaggedIsInShouldWork() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList where flagged in DEFAULT_FLAGGED or UPDATED_FLAGGED
        defaultReportStatusShouldBeFound("flagged.in=" + DEFAULT_FLAGGED + "," + UPDATED_FLAGGED);

        // Get all the reportStatusList where flagged equals to UPDATED_FLAGGED
        defaultReportStatusShouldNotBeFound("flagged.in=" + UPDATED_FLAGGED);
    }

    @Test
    @Transactional
    public void getAllReportStatusesByFlaggedIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportStatusRepository.saveAndFlush(reportStatus);

        // Get all the reportStatusList where flagged is not null
        defaultReportStatusShouldBeFound("flagged.specified=true");

        // Get all the reportStatusList where flagged is null
        defaultReportStatusShouldNotBeFound("flagged.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportStatusesByReporterIsEqualToSomething() throws Exception {
        // Initialize the database
        User reporter = UserResourceIntTest.createEntity(em);
        em.persist(reporter);
        em.flush();
        reportStatus.setReporter(reporter);
        reportStatusRepository.saveAndFlush(reportStatus);
        Long reporterId = reporter.getId();

        // Get all the reportStatusList where reporter equals to reporterId
        defaultReportStatusShouldBeFound("reporterId.equals=" + reporterId);

        // Get all the reportStatusList where reporter equals to reporterId + 1
        defaultReportStatusShouldNotBeFound("reporterId.equals=" + (reporterId + 1));
    }


    @Test
    @Transactional
    public void getAllReportStatusesByReportIsEqualToSomething() throws Exception {
        // Initialize the database
        Report report = ReportResourceIntTest.createEntity(em);
        em.persist(report);
        em.flush();
        reportStatus.setReport(report);
        reportStatusRepository.saveAndFlush(reportStatus);
        Long reportId = report.getId();

        // Get all the reportStatusList where report equals to reportId
        defaultReportStatusShouldBeFound("reportId.equals=" + reportId);

        // Get all the reportStatusList where report equals to reportId + 1
        defaultReportStatusShouldNotBeFound("reportId.equals=" + (reportId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultReportStatusShouldBeFound(String filter) throws Exception {
        restReportStatusMockMvc.perform(get("/api/report-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reportStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].saved").value(DEFAULT_SAVED.toString()))
            .andExpect(jsonPath("$.[*].flagged").value(DEFAULT_FLAGGED.toString()));

        // Check, that the count call also returns 1
        restReportStatusMockMvc.perform(get("/api/report-statuses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultReportStatusShouldNotBeFound(String filter) throws Exception {
        restReportStatusMockMvc.perform(get("/api/report-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReportStatusMockMvc.perform(get("/api/report-statuses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingReportStatus() throws Exception {
        // Get the reportStatus
        restReportStatusMockMvc.perform(get("/api/report-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReportStatus() throws Exception {
        // Initialize the database
        reportStatusService.save(reportStatus);

        int databaseSizeBeforeUpdate = reportStatusRepository.findAll().size();

        // Update the reportStatus
        ReportStatus updatedReportStatus = reportStatusRepository.findById(reportStatus.getId()).get();
        // Disconnect from session so that the updates on updatedReportStatus are not directly saved in db
        em.detach(updatedReportStatus);
        updatedReportStatus
            .saved(UPDATED_SAVED)
            .flagged(UPDATED_FLAGGED);

        restReportStatusMockMvc.perform(put("/api/report-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedReportStatus)))
            .andExpect(status().isOk());

        // Validate the ReportStatus in the database
        List<ReportStatus> reportStatusList = reportStatusRepository.findAll();
        assertThat(reportStatusList).hasSize(databaseSizeBeforeUpdate);
        ReportStatus testReportStatus = reportStatusList.get(reportStatusList.size() - 1);
        assertThat(testReportStatus.getSaved()).isEqualTo(UPDATED_SAVED);
        assertThat(testReportStatus.getFlagged()).isEqualTo(UPDATED_FLAGGED);
    }

    @Test
    @Transactional
    public void updateNonExistingReportStatus() throws Exception {
        int databaseSizeBeforeUpdate = reportStatusRepository.findAll().size();

        // Create the ReportStatus

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportStatusMockMvc.perform(put("/api/report-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportStatus)))
            .andExpect(status().isBadRequest());

        // Validate the ReportStatus in the database
        List<ReportStatus> reportStatusList = reportStatusRepository.findAll();
        assertThat(reportStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReportStatus() throws Exception {
        // Initialize the database
        reportStatusService.save(reportStatus);

        int databaseSizeBeforeDelete = reportStatusRepository.findAll().size();

        // Get the reportStatus
        restReportStatusMockMvc.perform(delete("/api/report-statuses/{id}", reportStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ReportStatus> reportStatusList = reportStatusRepository.findAll();
        assertThat(reportStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReportStatus.class);
        ReportStatus reportStatus1 = new ReportStatus();
        reportStatus1.setId(1L);
        ReportStatus reportStatus2 = new ReportStatus();
        reportStatus2.setId(reportStatus1.getId());
        assertThat(reportStatus1).isEqualTo(reportStatus2);
        reportStatus2.setId(2L);
        assertThat(reportStatus1).isNotEqualTo(reportStatus2);
        reportStatus1.setId(null);
        assertThat(reportStatus1).isNotEqualTo(reportStatus2);
    }
}
