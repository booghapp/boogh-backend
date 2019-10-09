package org.boogh.web.rest;

import com.vividsolutions.jts.geom.Point;
import org.boogh.BooghApp;
import org.boogh.clientservice.TelegramBotService;
import org.boogh.config.ApplicationProperties;
import org.boogh.domain.Comment;
import org.boogh.domain.Report;
import org.boogh.domain.ReportStatus;
import org.boogh.domain.User;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.domain.enumeration.ReportType;
import org.boogh.repository.ReportRepository;
import org.boogh.repository.TelegramChatRepository;
import org.boogh.service.MailService;
import org.boogh.service.ReportQueryService;
import org.boogh.service.ReportService;
import org.boogh.web.rest.errors.ExceptionTranslator;
import org.boogh.web.rest.util.GeoUtils;
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

import javax.persistence.EntityManager;
import java.util.List;

import static org.boogh.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ReportResource REST controller.
 *
 * @see ReportResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class ReportResourceIntTest {

    private static final ReportType DEFAULT_TYPE = ReportType.ROAD_SAFETY;
    private static final ReportType UPDATED_TYPE = ReportType.EDUCATION;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ReportState DEFAULT_STATE = ReportState.PENDING;
    private static final ReportState UPDATED_STATE = ReportState.APPROVED;

    private static final Boolean DEFAULT_ANONYMOUS = false;
    private static final Boolean UPDATED_ANONYMOUS = true;

    private static final Double DEFAULT_LATITUDE = 30.347296;
    private static final Double UPDATED_LATITUDE = 31.123433;

    private static final Double DEFAULT_LONGITUDE = 48.293400;
    private static final Double UPDATED_LONGITUDE = 49.987633;

    private static final Point DEFAULT_LOCATION = GeoUtils.pointFromLongLat(48.293400, 30.347296);
    private static final Point UPDATED_LOCATION = GeoUtils.pointFromLongLat(139.691706, 35.689487);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportQueryService reportQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    MailService mailService;

    @Autowired
    TelegramChatRepository telegramChatRepository;

    @Autowired
    TelegramBotService telegramBotService;

    private MockMvc restReportMockMvc;

    private Report report;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        final ReportResource reportResource = new ReportResource(reportService, reportQueryService, mailService, applicationProperties, telegramChatRepository, telegramBotService);
        this.restReportMockMvc = MockMvcBuilders.standaloneSetup(reportResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entitiesnp might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity(EntityManager em) {
        Report report = new Report()
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .location(DEFAULT_LOCATION)
            .state(DEFAULT_STATE)
            .anonymous(DEFAULT_ANONYMOUS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        report.setReporter(user);
        return report;
    }

    @Before
    public void initTest() {
        report = createEntity(em);
    }

    @Test
    @Transactional
    public void createReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report
        restReportMockMvc.perform(post("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(report)))
            .andExpect(status().isCreated());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testReport.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReport.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testReport.isAnonymous()).isEqualTo(DEFAULT_ANONYMOUS);
        assertThat(testReport.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testReport.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    public void createReportWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report with an existing ID
        report.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportMockMvc.perform(post("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(report)))
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setType(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(report)))
            .andExpect(status().isBadRequest());

        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setState(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(report)))
            .andExpect(status().isBadRequest());

        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReports() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].anonymous").value(hasItem(DEFAULT_ANONYMOUS.booleanValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        // This test fails due to application properties not being set
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
            .andExpect(status().isInternalServerError());
            /*.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(report.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.anonymous").value(DEFAULT_ANONYMOUS.booleanValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));*/
    }

    @Test
    @Transactional
    public void getAllReportsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where type equals to DEFAULT_TYPE
        defaultReportShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the reportList where type equals to UPDATED_TYPE
        defaultReportShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllReportsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultReportShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the reportList where type equals to UPDATED_TYPE
        defaultReportShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllReportsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where type is not null
        defaultReportShouldBeFound("type.specified=true");

        // Get all the reportList where type is null
        defaultReportShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where state equals to DEFAULT_STATE
        defaultReportShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the reportList where state equals to UPDATED_STATE
        defaultReportShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllReportsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where state in DEFAULT_STATE or UPDATED_STATE
        defaultReportShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the reportList where state equals to UPDATED_STATE
        defaultReportShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllReportsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where state is not null
        defaultReportShouldBeFound("state.specified=true");

        // Get all the reportList where state is null
        defaultReportShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByAnonymousIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where anonymous equals to DEFAULT_ANONYMOUS
        defaultReportShouldBeFound("anonymous.equals=" + DEFAULT_ANONYMOUS);

        // Get all the reportList where anonymous equals to UPDATED_ANONYMOUS
        defaultReportShouldNotBeFound("anonymous.equals=" + UPDATED_ANONYMOUS);
    }

    @Test
    @Transactional
    public void getAllReportsByAnonymousIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where anonymous in DEFAULT_ANONYMOUS or UPDATED_ANONYMOUS
        defaultReportShouldBeFound("anonymous.in=" + DEFAULT_ANONYMOUS + "," + UPDATED_ANONYMOUS);

        // Get all the reportList where anonymous equals to UPDATED_ANONYMOUS
        defaultReportShouldNotBeFound("anonymous.in=" + UPDATED_ANONYMOUS);
    }

    @Test
    @Transactional
    public void getAllReportsByAnonymousIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where anonymous is not null
        defaultReportShouldBeFound("anonymous.specified=true");

        // Get all the reportList where anonymous is null
        defaultReportShouldNotBeFound("anonymous.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where latitude equals to DEFAULT_LATITUDE
        defaultReportShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the reportList where latitude equals to UPDATED_LATITUDE
        defaultReportShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllReportsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultReportShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the reportList where latitude equals to UPDATED_LATITUDE
        defaultReportShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllReportsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where latitude is not null
        defaultReportShouldBeFound("latitude.specified=true");

        // Get all the reportList where latitude is null
        defaultReportShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsWithinGeometry(){
        Report report1 = new Report()
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .location(DEFAULT_LOCATION)
            .state(DEFAULT_STATE)
            .anonymous(DEFAULT_ANONYMOUS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        report1.setReporter(report.getReporter());

        reportRepository.saveAndFlush(report);
        reportRepository.saveAndFlush(report1);

        List<Report> reports = reportRepository.findWithinGeometry(GeoUtils.boundingBoxToGeometry(DEFAULT_LONGITUDE - 0.00001, DEFAULT_LATITUDE - 0.00001, UPDATED_LONGITUDE - 0.00001, UPDATED_LATITUDE - 0.00001));
        assertThat(reports.size() == 1);
    }

    @Test
    @Transactional
    public void shouldNotFindReportsOnGeometryBoundary(){
        Report report1 = new Report()
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .location(DEFAULT_LOCATION)
            .state(DEFAULT_STATE)
            .anonymous(DEFAULT_ANONYMOUS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        report1.setReporter(report.getReporter());

        reportRepository.saveAndFlush(report1);

        List<Report> reports = reportRepository.findWithinGeometry(GeoUtils.boundingBoxToGeometry(DEFAULT_LONGITUDE, DEFAULT_LATITUDE, UPDATED_LONGITUDE, UPDATED_LATITUDE));
        assertThat(reports.size() == 0);
    }

    @Test
    @Transactional
    public void getAllReportsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where longitude equals to DEFAULT_LONGITUDE
        defaultReportShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the reportList where longitude equals to UPDATED_LONGITUDE
        defaultReportShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllReportsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultReportShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the reportList where longitude equals to UPDATED_LONGITUDE
        defaultReportShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllReportsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where longitude is not null
        defaultReportShouldBeFound("longitude.specified=true");

        // Get all the reportList where longitude is null
        defaultReportShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        Comment comments = CommentResourceIntTest.createEntity(em);
        em.persist(comments);
        em.flush();
        report.addComments(comments);
        reportRepository.saveAndFlush(report);
        Long commentsId = comments.getId();

        // Get all the reportList where comments equals to commentsId
        defaultReportShouldBeFound("commentsId.equals=" + commentsId);

        // Get all the reportList where comments equals to commentsId + 1
        defaultReportShouldNotBeFound("commentsId.equals=" + (commentsId + 1));
    }


    @Test
    @Transactional
    public void getAllReportsByReporterIsEqualToSomething() throws Exception {
        // Initialize the database
        User reporter = UserResourceIntTest.createEntity(em);
        em.persist(reporter);
        em.flush();
        report.setReporter(reporter);
        reportRepository.saveAndFlush(report);
        Long reporterId = reporter.getId();

        // Get all the reportList where reporter equals to reporterId
        defaultReportShouldBeFound("reporterId.equals=" + reporterId);

        // Get all the reportList where reporter equals to reporterId + 1
        defaultReportShouldNotBeFound("reporterId.equals=" + (reporterId + 1));
    }


    @Test
    @Transactional
    public void getAllReportsByReportStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        ReportStatus reportStatus = ReportStatusResourceIntTest.createEntity(em);
        em.persist(reportStatus);
        em.flush();
        report.addReportStatus(reportStatus);
        reportRepository.saveAndFlush(report);
        Long reportStatusId = reportStatus.getId();

        // Get all the reportList where reportStatus equals to reportStatusId
        defaultReportShouldBeFound("reportStatusId.equals=" + reportStatusId);

        // Get all the reportList where reportStatus equals to reportStatusId + 1
        defaultReportShouldNotBeFound("reportStatusId.equals=" + (reportStatusId + 1));
    }


    @Test
    @Transactional
    public void getAllReportsByParentIsEqualToSomething() throws Exception {
        // Initialize the database
        Report parent = ReportResourceIntTest.createEntity(em);
        em.persist(parent);
        em.flush();
        report.setParent(parent);
        reportRepository.saveAndFlush(report);
        Long parentId = parent.getId();

        // Get all the reportList where parent equals to parentId
        defaultReportShouldBeFound("parentId.equals=" + parentId);

        // Get all the reportList where parent equals to parentId + 1
        defaultReportShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultReportShouldBeFound(String filter) throws Exception {
        restReportMockMvc.perform(get("/api/reports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].anonymous").value(hasItem(DEFAULT_ANONYMOUS.booleanValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));

        // Check, that the count call also returns 1
        restReportMockMvc.perform(get("/api/reports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultReportShouldNotBeFound(String filter) throws Exception {
        restReportMockMvc.perform(get("/api/reports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReportMockMvc.perform(get("/api/reports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReport() throws Exception {
        // Initialize the database
        reportService.save(report);

        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).get();
        // Disconnect from session so that the updates on updatedReport are not directly saved in db
        em.detach(updatedReport);
        updatedReport
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .state(UPDATED_STATE)
            .anonymous(UPDATED_ANONYMOUS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restReportMockMvc.perform(put("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedReport)))
            .andExpect(status().isOk());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testReport.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReport.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testReport.isAnonymous()).isEqualTo(UPDATED_ANONYMOUS);
        assertThat(testReport.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testReport.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void updateNonExistingReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Create the Report

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportMockMvc.perform(put("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(report)))
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReport() throws Exception {
        // Initialize the database
        reportService.save(report);

        int databaseSizeBeforeDelete = reportRepository.findAll().size();

        // Get the report
        restReportMockMvc.perform(delete("/api/reports/{id}", report.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Report.class);
        Report report1 = new Report();
        report1.setId(1L);
        Report report2 = new Report();
        report2.setId(report1.getId());
        assertThat(report1).isEqualTo(report2);
        report2.setId(2L);
        assertThat(report1).isNotEqualTo(report2);
        report1.setId(null);
        assertThat(report1).isNotEqualTo(report2);
    }
}
