package org.boogh.web.rest.errors;

public class EmptyCommentException extends BadRequestAlertException {

    public EmptyCommentException() {
        super(ErrorConstants.COMMENT_EMPTY_TYPE, "Comment cannot be empty", "userManagement", "Comment cannot be empty");
    }
}
