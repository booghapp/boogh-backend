package org.boogh.web.rest.errors;

public class ReportLocationMissingException extends BadRequestAlertException{

    private static final long serialVersionUID = 1L;

    public ReportLocationMissingException() {
        super(ErrorConstants.REPORT_LOCATION_MISSING_TYPE, "Report location is missing!", "userManagement", "report location missing");
    }
}
