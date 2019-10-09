package org.boogh.clientservice.mapper;
import org.boogh.clientservice.dto.VoteDTO;
import org.boogh.domain.Vote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VoteMapper {
    public VoteDTO voteToVoteDTO(Vote vote) {
        return new VoteDTO(vote);
    }

    public List<VoteDTO> votesToVoteDTOs(List<Vote> votes) {
        return votes.stream()
            .filter(Objects::nonNull)
            .map(this::voteToVoteDTO)
            .collect(Collectors.toList());
    }

    public Vote voteDTOToVote(VoteDTO voteDTO) {
        if (voteDTO == null) {
            return null;
        } else {
            Vote vote = new Vote();
            vote.setId(voteDTO.getId());
            ClientUserMapper userMapper = new ClientUserMapper();
            vote.setVoter(userMapper.userDTOToUser(voteDTO.getVoter()));
            CommentMapper commentMapper = new CommentMapper();
            vote.setComment(commentMapper.commentDTOToComment(voteDTO.getComment()));
            return vote;
        }
    }

    public List<Vote> voteDTOsToVotes(List<VoteDTO> voteDTOs){
        return voteDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::voteDTOToVote)
            .collect(Collectors.toList());
    }
}
