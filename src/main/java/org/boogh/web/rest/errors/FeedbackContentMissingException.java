package org.boogh.web.rest.errors;

public class FeedbackContentMissingException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public FeedbackContentMissingException() {
        super(ErrorConstants.FEEDBACK_CONTENT_EMPTY_TYPE, "Feedback content is missing!", "feedback", "feedback content missing");
    }
}
