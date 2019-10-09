package org.boogh.web.rest.errors;

public class UpdateDescriptionMissingException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UpdateDescriptionMissingException() {
        super(ErrorConstants.UPDATE_DESCRIPTION_MISSING_TYPE, "Update description is missing!", "userManagement", "update description missing");
    }
}
