package org.boogh.clientservice.dto;

import org.boogh.clientservice.mapper.ClientUserMapper;
import org.boogh.clientservice.mapper.CommentMapper;
import org.boogh.domain.Vote;

public class VoteDTO {

    private Long id;

    private Integer vote;

    private UserDTO voter;

    private CommentDTO comment;

    public VoteDTO(Vote vote) {
        this.id = vote.getId();
        this.vote = vote.getVote();
        ClientUserMapper userMapper = new ClientUserMapper();
        this.voter = userMapper.userToUserDTO(vote.getVoter(), false);
        CommentMapper commentMapper = new CommentMapper();
        this.comment = commentMapper.commentToCommentDTO(vote.getComment());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public UserDTO getVoter() {
        return voter;
    }

    public void setVoter(UserDTO voter) {
        this.voter = voter;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }
}
