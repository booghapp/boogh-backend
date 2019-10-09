package org.boogh.clientservice;

import org.boogh.clientservice.dto.CommentDTO;

import java.util.ArrayList;
import java.util.List;

public class CommentNode {
    private List<CommentNode> children;

    private CommentDTO comment;

    public CommentNode(CommentDTO comment) {
        this.comment = comment;
        this.children = new ArrayList<>();
    }

    public void addChild(CommentNode child) {
        children.add(child);
    }

    public List<CommentNode> getChildren() {
        return children;
    }

    public void setChildren(List<CommentNode> children) {
        this.children = children;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }
}
