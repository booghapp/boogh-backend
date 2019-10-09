package org.boogh.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");
    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final URI EMAIL_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/email-not-found");
    public static final URI REPORT_TITLE_MISSING_TYPE = URI.create(PROBLEM_BASE_URL + "/report-title-missing");
    public static final URI REPORT_LOCATION_MISSING_TYPE = URI.create(PROBLEM_BASE_URL + "/report-location-missing");
    public static final URI UPDATE_DESCRIPTION_MISSING_TYPE = URI.create(PROBLEM_BASE_URL + "/update-description-missing");
    public static final URI COMMENT_EMPTY_TYPE = URI.create(PROBLEM_BASE_URL + "/comment-empty");
    public static final URI FEEDBACK_CONTENT_EMPTY_TYPE = URI.create(PROBLEM_BASE_URL + "/feedback-content-empty");

    private ErrorConstants() {
    }
}
