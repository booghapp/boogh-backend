package org.boogh.clientservice;

import org.boogh.clientservice.dto.CommentDTO;
import org.boogh.domain.Comment;

import java.util.List;

public class CommentTree {
    private List<CommentDTO> comments;
    private CommentNode head;

    public CommentTree(List<CommentDTO> comments) {
        this.comments = comments;
        head = new CommentNode(new CommentDTO(new Comment()));
    }

    public void createCommentTree(CommentNode commentNode) {
        if (commentNode == head) {
            for (CommentDTO comment: comments) {
                if (comment.getParent() == null) {
                    CommentNode child = new CommentNode(comment);
                    head.addChild(child);
                    createCommentTree(child);
                }
            }
        } else {
            for (CommentDTO comment: comments) {
                CommentDTO parent = comment.getParent();
                if (parent != null && commentNode.getComment().getId() == parent.getId()) {
                    CommentNode child = new CommentNode(comment);
                    commentNode.addChild(child);
                    createCommentTree(child);
                }
            }
        }
    }

    public CommentNode getHead() {
        return head;
    }

    public void setHead(CommentNode head) {
        this.head = head;
    }
}

