package org.boogh.web.rest.errors;

public class ReportTitleMissingException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ReportTitleMissingException() {
        super(ErrorConstants.REPORT_TITLE_MISSING_TYPE, "Report title is missing!", "userManagement", "report title missing");
    }
}
