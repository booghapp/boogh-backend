package org.boogh.clientservice.mapper;

import org.boogh.clientservice.dto.CommentDTO;
import org.boogh.domain.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentMapper {

    public CommentDTO commentToCommentDTO(Comment comment) {
        return new CommentDTO(comment);
    }

    public List<CommentDTO> commentsToCommentDTOs(List<Comment> comments) {
        return comments.stream()
            .filter(Objects::nonNull)
            .map(this::commentToCommentDTO)
            .collect(Collectors.toList());
    }

    public Comment commentDTOToComment(CommentDTO commentDTO) {
        if (commentDTO == null) {
            return null;
        } else {
            Comment comment = new Comment();
            comment.setId(commentDTO.getId());
            ClientUserMapper userMapper = new ClientUserMapper();
            comment.setCommenter(userMapper.userDTOToUser(commentDTO.getCommenter()));

            return comment;
        }
    }

    public List<Comment> commentDTOsToComments(List<CommentDTO> commentDTOs){
        return commentDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::commentDTOToComment)
            .collect(Collectors.toList());
    }
}

