package org.boogh.web.rest;

import org.boogh.BooghApp;
import org.boogh.domain.Comment;
import org.boogh.domain.User;
import org.boogh.domain.Vote;
import org.boogh.repository.VoteRepository;
import org.boogh.service.VoteQueryService;
import org.boogh.service.VoteService;
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
 * Test class for the VoteResource REST controller.
 *
 * @see VoteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooghApp.class)
public class VoteResourceIntTest {

    private static final Integer DEFAULT_VOTE = 1;
    private static final Integer UPDATED_VOTE = 2;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteQueryService voteQueryService;

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

    private MockMvc restVoteMockMvc;

    private Vote vote;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VoteResource voteResource = new VoteResource(voteService, voteQueryService);
        this.restVoteMockMvc = MockMvcBuilders.standaloneSetup(voteResource)
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
    public static Vote createEntity(EntityManager em) {
        Vote vote = new Vote()
            .vote(DEFAULT_VOTE);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        vote.setVoter(user);
        // Add required entity
        Comment comment = CommentResourceIntTest.createEntity(em);
        em.persist(comment);
        em.flush();
        vote.setComment(comment);
        return vote;
    }

    @Before
    public void initTest() {
        vote = createEntity(em);
    }

    @Test
    @Transactional
    public void createVote() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // Create the Vote
        restVoteMockMvc.perform(post("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isCreated());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate + 1);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getVote()).isEqualTo(DEFAULT_VOTE);
    }

    @Test
    @Transactional
    public void createVoteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // Create the Vote with an existing ID
        vote.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoteMockMvc.perform(post("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllVotes() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
            .andExpect(jsonPath("$.[*].vote").value(hasItem(DEFAULT_VOTE)));
    }
    
    @Test
    @Transactional
    public void getVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", vote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vote.getId().intValue()))
            .andExpect(jsonPath("$.vote").value(DEFAULT_VOTE));
    }

    @Test
    @Transactional
    public void getAllVotesByVoteIsEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where vote equals to DEFAULT_VOTE
        defaultVoteShouldBeFound("vote.equals=" + DEFAULT_VOTE);

        // Get all the voteList where vote equals to UPDATED_VOTE
        defaultVoteShouldNotBeFound("vote.equals=" + UPDATED_VOTE);
    }

    @Test
    @Transactional
    public void getAllVotesByVoteIsInShouldWork() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where vote in DEFAULT_VOTE or UPDATED_VOTE
        defaultVoteShouldBeFound("vote.in=" + DEFAULT_VOTE + "," + UPDATED_VOTE);

        // Get all the voteList where vote equals to UPDATED_VOTE
        defaultVoteShouldNotBeFound("vote.in=" + UPDATED_VOTE);
    }

    @Test
    @Transactional
    public void getAllVotesByVoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where vote is not null
        defaultVoteShouldBeFound("vote.specified=true");

        // Get all the voteList where vote is null
        defaultVoteShouldNotBeFound("vote.specified=false");
    }

    @Test
    @Transactional
    public void getAllVotesByVoteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where vote greater than or equals to DEFAULT_VOTE
        defaultVoteShouldBeFound("vote.greaterOrEqualThan=" + DEFAULT_VOTE);

        // Get all the voteList where vote greater than or equals to UPDATED_VOTE
        defaultVoteShouldNotBeFound("vote.greaterOrEqualThan=" + UPDATED_VOTE);
    }

    @Test
    @Transactional
    public void getAllVotesByVoteIsLessThanSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where vote less than or equals to DEFAULT_VOTE
        defaultVoteShouldNotBeFound("vote.lessThan=" + DEFAULT_VOTE);

        // Get all the voteList where vote less than or equals to UPDATED_VOTE
        defaultVoteShouldBeFound("vote.lessThan=" + UPDATED_VOTE);
    }


    @Test
    @Transactional
    public void getAllVotesByVoterIsEqualToSomething() throws Exception {
        // Initialize the database
        User voter = UserResourceIntTest.createEntity(em);
        em.persist(voter);
        em.flush();
        vote.setVoter(voter);
        voteRepository.saveAndFlush(vote);
        Long voterId = voter.getId();

        // Get all the voteList where voter equals to voterId
        defaultVoteShouldBeFound("voterId.equals=" + voterId);

        // Get all the voteList where voter equals to voterId + 1
        defaultVoteShouldNotBeFound("voterId.equals=" + (voterId + 1));
    }


    @Test
    @Transactional
    public void getAllVotesByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        Comment comment = CommentResourceIntTest.createEntity(em);
        em.persist(comment);
        em.flush();
        vote.setComment(comment);
        voteRepository.saveAndFlush(vote);
        Long commentId = comment.getId();

        // Get all the voteList where comment equals to commentId
        defaultVoteShouldBeFound("commentId.equals=" + commentId);

        // Get all the voteList where comment equals to commentId + 1
        defaultVoteShouldNotBeFound("commentId.equals=" + (commentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultVoteShouldBeFound(String filter) throws Exception {
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
            .andExpect(jsonPath("$.[*].vote").value(hasItem(DEFAULT_VOTE)));

        // Check, that the count call also returns 1
        restVoteMockMvc.perform(get("/api/votes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultVoteShouldNotBeFound(String filter) throws Exception {
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVoteMockMvc.perform(get("/api/votes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingVote() throws Exception {
        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote
        Vote updatedVote = voteRepository.findById(vote.getId()).get();
        // Disconnect from session so that the updates on updatedVote are not directly saved in db
        em.detach(updatedVote);
        updatedVote
            .vote(UPDATED_VOTE);

        restVoteMockMvc.perform(put("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVote)))
            .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getVote()).isEqualTo(UPDATED_VOTE);
    }

    @Test
    @Transactional
    public void updateNonExistingVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Create the Vote

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoteMockMvc.perform(put("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        int databaseSizeBeforeDelete = voteRepository.findAll().size();

        // Delete the vote
        restVoteMockMvc.perform(delete("/api/votes/{id}", vote.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vote.class);
        Vote vote1 = new Vote();
        vote1.setId(1L);
        Vote vote2 = new Vote();
        vote2.setId(vote1.getId());
        assertThat(vote1).isEqualTo(vote2);
        vote2.setId(2L);
        assertThat(vote1).isNotEqualTo(vote2);
        vote1.setId(null);
        assertThat(vote1).isNotEqualTo(vote2);
    }
}
