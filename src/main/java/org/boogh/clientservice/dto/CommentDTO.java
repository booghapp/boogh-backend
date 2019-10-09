package org.boogh.clientservice.dto;


import org.boogh.domain.Comment;

import java.time.format.DateTimeFormatter;


public class CommentDTO {

    private Long id;

    private String content;

    private String date;

    private UserDTO commenter;

    private SlimReportDTO report;

    private CommentDTO parent;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        if(comment.getDate()!= null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            this.date = comment.getDate().format(formatter);
        }
        this.commenter = comment.getCommenter() != null ? new UserDTO(comment.getCommenter(), false) : null ;
        if (comment.getReport() != null) {
            SlimReportDTO commentReport = new SlimReportDTO(comment.getReport());
            this.report = commentReport;
        } else {
            this.report = null;
        }
        this.parent = comment.getParent() != null ? new CommentDTO(comment.getParent()) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserDTO getCommenter() {
        return commenter;
    }

    public void setCommenter(UserDTO commenter) {
        this.commenter = commenter;
    }

    public SlimReportDTO getReport() {
        return report;
    }

    public void setReport(SlimReportDTO report) {
        this.report = report;
    }

    public CommentDTO getParent() {
        return parent;
    }

    public void setParent(CommentDTO parent) {
        this.parent = parent;
    }
}
