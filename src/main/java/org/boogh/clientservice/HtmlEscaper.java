package org.boogh.clientservice;

import org.boogh.domain.Comment;
import org.boogh.domain.Feedback;
import org.boogh.domain.Report;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

public class HtmlEscaper {

    public static void escapeReport(Report report) {

        List<String> sanitizedImgs = new ArrayList<>();
        for (String base64: report.getImages()) {
            sanitizedImgs.add(HtmlUtils.htmlEscape(base64));
        }
        report.setImages(sanitizedImgs);

        report.setDescription(HtmlUtils.htmlEscape(report.getDescription()));
        report.setTitle(HtmlUtils.htmlEscape(report.getTitle()));
    }

    public static void escapeComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
    }

    public static void escapeFeedback(Feedback feedback) {
        feedback.setContent(HtmlUtils.htmlEscape(feedback.getContent()));
    }
}
