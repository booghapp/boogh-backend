package org.boogh.clientservice.dto;


import org.boogh.clientservice.mapper.ClientUserMapper;
import org.boogh.clientservice.mapper.ReportMapper;
import org.boogh.domain.Comment;
import org.boogh.domain.Report;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.domain.enumeration.ReportType;

import javax.persistence.Lob;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class ReportDTO{

    private Long id;

    private ReportType type;

    private String title;
    @Lob
    private String description;

    private Boolean anonymous;

    private Double longitude;

    private Double latitude;

    private String date;

    private ReportState state;

    private UserDTO reporter;

    private ReportDTO parent;

    private Set<Comment> comments;

    private boolean isFavoritedByCurrentUser;

    private boolean isCurrentUserReport;

    private List<String> images;

    public ReportDTO(Report report){
        this.id = report.getId();
        this.type = report.getType();
        this.title = report.getTitle();
        this.description = report.getDescription();
        this.anonymous = report.isAnonymous();
        this.longitude = report.getLongitude();
        this.latitude = report.getLatitude();
        if(report.getDate()!= null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            this.date = report.getDate().format(formatter);
        }
        this.state = report.getState();
        if(anonymous != null && !anonymous && report.getReporter() != null){
            ClientUserMapper userMapper = new ClientUserMapper();
            this.reporter = userMapper.userToUserDTO(report.getReporter(), false);
        }
        if(report.getParent() != null){
            ReportMapper reportMapper = new ReportMapper();
            this.parent = reportMapper.reportToReportDTO(report.getParent());
        }
        this.comments = report.getComments();
        this.isFavoritedByCurrentUser = report.isFavoritedByCurrentUser();
        this.isCurrentUserReport = report.isCurrentUsersReport();
        this.images = report.getImages();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ReportState getState() {
        return state;
    }

    public void setState(ReportState state) {
        this.state = state;
    }

    public UserDTO getReporter() {
        return reporter;
    }

    public void setReporter(UserDTO reporter) {
        this.reporter = reporter;
    }

    public ReportDTO getParent() {
        return parent;
    }

    public void setParent(ReportDTO parent) {
        this.parent = parent;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public boolean isFavoritedByCurrentUser() {
        return isFavoritedByCurrentUser;
    }

    public void setFavoritedByCurrentUser(boolean favoritedByCurrentUser) {
        isFavoritedByCurrentUser = favoritedByCurrentUser;
    }

    public boolean isCurrentUserReport() {
        return isCurrentUserReport;
    }

    public void setCurrentUserReport(boolean currentUserReport) {
        isCurrentUserReport = currentUserReport;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
