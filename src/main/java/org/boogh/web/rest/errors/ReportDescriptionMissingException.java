package org.boogh.web.rest.errors;

public class ReportDescriptionMissingException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ReportDescriptionMissingException() {
        super(ErrorConstants.REPORT_TITLE_MISSING_TYPE, "Report description is missing!", "userManagement", "report description missing");
    }
}
