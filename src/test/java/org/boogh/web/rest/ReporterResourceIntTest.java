package org.boogh.web.rest;

import org.boogh.BooghApp;
import org.boogh.domain.Reporter;
import org.boogh.domain.User;
import org.boogh.repository.ReporterRepository;
import org.boogh.service.ReporterQueryService;
import org.boogh.service.ReporterService;
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

import javax.persistence.EntityManager;
import java.util.List;

import static org.boogh.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReporterResource REST controller.
 *
 * @see ReporterResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class ReporterResourceIntTest {

    private static final String DEFAULT_ABOUT = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT = "BBBBBBBBBB";

    private static final Integer DEFAULT_KARMA = 1;
    private static final Integer UPDATED_KARMA = 2;

    private static final Boolean DEFAULT_VISIBILITY = false;
    private static final Boolean UPDATED_VISIBILITY = true;

    private static final Boolean DEFAULT_MODERATOR = false;
    private static final Boolean UPDATED_MODERATOR = true;

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_NOTIFICATIONS_ON = false;
    private static final Boolean UPDATED_NOTIFICATIONS_ON = true;

    @Autowired
    private ReporterRepository reporterRepository;

    @Autowired
    private ReporterService reporterService;

    @Autowired
    private ReporterQueryService reporterQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restReporterMockMvc;

    private Reporter reporter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ReporterResource reporterResource = new ReporterResource(reporterService, reporterQueryService);
        this.restReporterMockMvc = MockMvcBuilders.standaloneSetup(reporterResource)
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
    public static Reporter createEntity(EntityManager em) {
        Reporter reporter = new Reporter()
            .about(DEFAULT_ABOUT)
            .karma(DEFAULT_KARMA)
            .visibility(DEFAULT_VISIBILITY)
            .moderator(DEFAULT_MODERATOR)
            .location(DEFAULT_LOCATION)
            .notificationsOn(DEFAULT_NOTIFICATIONS_ON);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        reporter.setUser(user);
        return reporter;
    }

    @Before
    public void initTest() {
        reporter = createEntity(em);
    }

    @Test
    @Transactional
    public void createReporter() throws Exception {
        int databaseSizeBeforeCreate = reporterRepository.findAll().size();

        // Create the Reporter
        restReporterMockMvc.perform(post("/api/reporters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reporter)))
            .andExpect(status().isCreated());

        // Validate the Reporter in the database
        List<Reporter> reporterList = reporterRepository.findAll();
        assertThat(reporterList).hasSize(databaseSizeBeforeCreate + 1);
        Reporter testReporter = reporterList.get(reporterList.size() - 1);
        assertThat(testReporter.getAbout()).isEqualTo(DEFAULT_ABOUT);
        assertThat(testReporter.getKarma()).isEqualTo(DEFAULT_KARMA);
        assertThat(testReporter.isVisibility()).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testReporter.isModerator()).isEqualTo(DEFAULT_MODERATOR);
        assertThat(testReporter.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testReporter.isNotificationsOn()).isEqualTo(DEFAULT_NOTIFICATIONS_ON);
    }

    @Test
    @Transactional
    public void createReporterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reporterRepository.findAll().size();

        // Create the Reporter with an existing ID
        reporter.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReporterMockMvc.perform(post("/api/reporters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reporter)))
            .andExpect(status().isBadRequest());

        // Validate the Reporter in the database
        List<Reporter> reporterList = reporterRepository.findAll();
        assertThat(reporterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllReporters() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList
        restReporterMockMvc.perform(get("/api/reporters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reporter.getId().intValue())))
            .andExpect(jsonPath("$.[*].about").value(hasItem(DEFAULT_ABOUT.toString())))
            .andExpect(jsonPath("$.[*].karma").value(hasItem(DEFAULT_KARMA)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.booleanValue())))
            .andExpect(jsonPath("$.[*].moderator").value(hasItem(DEFAULT_MODERATOR.booleanValue())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].notificationsOn").value(hasItem(DEFAULT_NOTIFICATIONS_ON.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getReporter() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get the reporter
        restReporterMockMvc.perform(get("/api/reporters/{id}", reporter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(reporter.getId().intValue()))
            .andExpect(jsonPath("$.about").value(DEFAULT_ABOUT.toString()))
            .andExpect(jsonPath("$.karma").value(DEFAULT_KARMA))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.booleanValue()))
            .andExpect(jsonPath("$.moderator").value(DEFAULT_MODERATOR.booleanValue()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.notificationsOn").value(DEFAULT_NOTIFICATIONS_ON.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllReportersByKarmaIsEqualToSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where karma equals to DEFAULT_KARMA
        defaultReporterShouldBeFound("karma.equals=" + DEFAULT_KARMA);

        // Get all the reporterList where karma equals to UPDATED_KARMA
        defaultReporterShouldNotBeFound("karma.equals=" + UPDATED_KARMA);
    }

    @Test
    @Transactional
    public void getAllReportersByKarmaIsInShouldWork() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where karma in DEFAULT_KARMA or UPDATED_KARMA
        defaultReporterShouldBeFound("karma.in=" + DEFAULT_KARMA + "," + UPDATED_KARMA);

        // Get all the reporterList where karma equals to UPDATED_KARMA
        defaultReporterShouldNotBeFound("karma.in=" + UPDATED_KARMA);
    }

    @Test
    @Transactional
    public void getAllReportersByKarmaIsNullOrNotNull() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where karma is not null
        defaultReporterShouldBeFound("karma.specified=true");

        // Get all the reporterList where karma is null
        defaultReporterShouldNotBeFound("karma.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportersByKarmaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where karma greater than or equals to DEFAULT_KARMA
        defaultReporterShouldBeFound("karma.greaterOrEqualThan=" + DEFAULT_KARMA);

        // Get all the reporterList where karma greater than or equals to UPDATED_KARMA
        defaultReporterShouldNotBeFound("karma.greaterOrEqualThan=" + UPDATED_KARMA);
    }

    @Test
    @Transactional
    public void getAllReportersByKarmaIsLessThanSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where karma less than or equals to DEFAULT_KARMA
        defaultReporterShouldNotBeFound("karma.lessThan=" + DEFAULT_KARMA);

        // Get all the reporterList where karma less than or equals to UPDATED_KARMA
        defaultReporterShouldBeFound("karma.lessThan=" + UPDATED_KARMA);
    }


    @Test
    @Transactional
    public void getAllReportersByVisibilityIsEqualToSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where visibility equals to DEFAULT_VISIBILITY
        defaultReporterShouldBeFound("visibility.equals=" + DEFAULT_VISIBILITY);

        // Get all the reporterList where visibility equals to UPDATED_VISIBILITY
        defaultReporterShouldNotBeFound("visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllReportersByVisibilityIsInShouldWork() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where visibility in DEFAULT_VISIBILITY or UPDATED_VISIBILITY
        defaultReporterShouldBeFound("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY);

        // Get all the reporterList where visibility equals to UPDATED_VISIBILITY
        defaultReporterShouldNotBeFound("visibility.in=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllReportersByVisibilityIsNullOrNotNull() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where visibility is not null
        defaultReporterShouldBeFound("visibility.specified=true");

        // Get all the reporterList where visibility is null
        defaultReporterShouldNotBeFound("visibility.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportersByModeratorIsEqualToSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where moderator equals to DEFAULT_MODERATOR
        defaultReporterShouldBeFound("moderator.equals=" + DEFAULT_MODERATOR);

        // Get all the reporterList where moderator equals to UPDATED_MODERATOR
        defaultReporterShouldNotBeFound("moderator.equals=" + UPDATED_MODERATOR);
    }

    @Test
    @Transactional
    public void getAllReportersByModeratorIsInShouldWork() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where moderator in DEFAULT_MODERATOR or UPDATED_MODERATOR
        defaultReporterShouldBeFound("moderator.in=" + DEFAULT_MODERATOR + "," + UPDATED_MODERATOR);

        // Get all the reporterList where moderator equals to UPDATED_MODERATOR
        defaultReporterShouldNotBeFound("moderator.in=" + UPDATED_MODERATOR);
    }

    @Test
    @Transactional
    public void getAllReportersByModeratorIsNullOrNotNull() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where moderator is not null
        defaultReporterShouldBeFound("moderator.specified=true");

        // Get all the reporterList where moderator is null
        defaultReporterShouldNotBeFound("moderator.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportersByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where location equals to DEFAULT_LOCATION
        defaultReporterShouldBeFound("location.equals=" + DEFAULT_LOCATION);

        // Get all the reporterList where location equals to UPDATED_LOCATION
        defaultReporterShouldNotBeFound("location.equals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void getAllReportersByLocationIsInShouldWork() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where location in DEFAULT_LOCATION or UPDATED_LOCATION
        defaultReporterShouldBeFound("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION);

        // Get all the reporterList where location equals to UPDATED_LOCATION
        defaultReporterShouldNotBeFound("location.in=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void getAllReportersByLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where location is not null
        defaultReporterShouldBeFound("location.specified=true");

        // Get all the reporterList where location is null
        defaultReporterShouldNotBeFound("location.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportersByNotificationsOnIsEqualToSomething() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where notificationsOn equals to DEFAULT_NOTIFICATIONS_ON
        defaultReporterShouldBeFound("notificationsOn.equals=" + DEFAULT_NOTIFICATIONS_ON);

        // Get all the reporterList where notificationsOn equals to UPDATED_NOTIFICATIONS_ON
        defaultReporterShouldNotBeFound("notificationsOn.equals=" + UPDATED_NOTIFICATIONS_ON);
    }

    @Test
    @Transactional
    public void getAllReportersByNotificationsOnIsInShouldWork() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where notificationsOn in DEFAULT_NOTIFICATIONS_ON or UPDATED_NOTIFICATIONS_ON
        defaultReporterShouldBeFound("notificationsOn.in=" + DEFAULT_NOTIFICATIONS_ON + "," + UPDATED_NOTIFICATIONS_ON);

        // Get all the reporterList where notificationsOn equals to UPDATED_NOTIFICATIONS_ON
        defaultReporterShouldNotBeFound("notificationsOn.in=" + UPDATED_NOTIFICATIONS_ON);
    }

    @Test
    @Transactional
    public void getAllReportersByNotificationsOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        reporterRepository.saveAndFlush(reporter);

        // Get all the reporterList where notificationsOn is not null
        defaultReporterShouldBeFound("notificationsOn.specified=true");

        // Get all the reporterList where notificationsOn is null
        defaultReporterShouldNotBeFound("notificationsOn.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportersByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        reporter.setUser(user);
        reporterRepository.saveAndFlush(reporter);
        Long userId = user.getId();

        // Get all the reporterList where user equals to userId
        defaultReporterShouldBeFound("userId.equals=" + userId);

        // Get all the reporterList where user equals to userId + 1
        defaultReporterShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultReporterShouldBeFound(String filter) throws Exception {
        restReporterMockMvc.perform(get("/api/reporters?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reporter.getId().intValue())))
            .andExpect(jsonPath("$.[*].about").value(hasItem(DEFAULT_ABOUT.toString())))
            .andExpect(jsonPath("$.[*].karma").value(hasItem(DEFAULT_KARMA)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.booleanValue())))
            .andExpect(jsonPath("$.[*].moderator").value(hasItem(DEFAULT_MODERATOR.booleanValue())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].notificationsOn").value(hasItem(DEFAULT_NOTIFICATIONS_ON.booleanValue())));

        // Check, that the count call also returns 1
        restReporterMockMvc.perform(get("/api/reporters/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultReporterShouldNotBeFound(String filter) throws Exception {
        restReporterMockMvc.perform(get("/api/reporters?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReporterMockMvc.perform(get("/api/reporters/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingReporter() throws Exception {
        // Get the reporter
        restReporterMockMvc.perform(get("/api/reporters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReporter() throws Exception {
        // Initialize the database
        reporterService.save(reporter);

        int databaseSizeBeforeUpdate = reporterRepository.findAll().size();

        // Update the reporter
        Reporter updatedReporter = reporterRepository.findById(reporter.getId()).get();
        // Disconnect from session so that the updates on updatedReporter are not directly saved in db
        em.detach(updatedReporter);
        updatedReporter
            .about(UPDATED_ABOUT)
            .karma(UPDATED_KARMA)
            .visibility(UPDATED_VISIBILITY)
            .moderator(UPDATED_MODERATOR)
            .location(UPDATED_LOCATION)
            .notificationsOn(UPDATED_NOTIFICATIONS_ON);

        restReporterMockMvc.perform(put("/api/reporters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedReporter)))
            .andExpect(status().isOk());

        // Validate the Reporter in the database
        List<Reporter> reporterList = reporterRepository.findAll();
        assertThat(reporterList).hasSize(databaseSizeBeforeUpdate);
        Reporter testReporter = reporterList.get(reporterList.size() - 1);
        assertThat(testReporter.getAbout()).isEqualTo(UPDATED_ABOUT);
        assertThat(testReporter.getKarma()).isEqualTo(UPDATED_KARMA);
        assertThat(testReporter.isVisibility()).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testReporter.isModerator()).isEqualTo(UPDATED_MODERATOR);
        assertThat(testReporter.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testReporter.isNotificationsOn()).isEqualTo(UPDATED_NOTIFICATIONS_ON);
    }

    @Test
    @Transactional
    public void updateNonExistingReporter() throws Exception {
        int databaseSizeBeforeUpdate = reporterRepository.findAll().size();

        // Create the Reporter

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReporterMockMvc.perform(put("/api/reporters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reporter)))
            .andExpect(status().isBadRequest());

        // Validate the Reporter in the database
        List<Reporter> reporterList = reporterRepository.findAll();
        assertThat(reporterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReporter() throws Exception {
        // Initialize the database
        reporterService.save(reporter);

        int databaseSizeBeforeDelete = reporterRepository.findAll().size();

        // Get the reporter
        restReporterMockMvc.perform(delete("/api/reporters/{id}", reporter.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Reporter> reporterList = reporterRepository.findAll();
        assertThat(reporterList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reporter.class);
        Reporter reporter1 = new Reporter();
        reporter1.setId(1L);
        Reporter reporter2 = new Reporter();
        reporter2.setId(reporter1.getId());
        assertThat(reporter1).isEqualTo(reporter2);
        reporter2.setId(2L);
        assertThat(reporter1).isNotEqualTo(reporter2);
        reporter1.setId(null);
        assertThat(reporter1).isNotEqualTo(reporter2);
    }
}
