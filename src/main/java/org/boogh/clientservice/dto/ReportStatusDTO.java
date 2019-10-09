package org.boogh.clientservice.dto;

import org.boogh.clientservice.mapper.ClientUserMapper;
import org.boogh.clientservice.mapper.ReportMapper;
import org.boogh.domain.ReportStatus;
import org.boogh.domain.enumeration.ReportStatusState;

public class ReportStatusDTO {

    private Long id;

    private ReportStatusState saved;

    private ReportStatusState flagged;

    private UserDTO reporter;

    private ReportDTO report;

    public ReportStatusDTO(ReportStatus reportStatus){
        this.id = reportStatus.getId();
        this.saved = reportStatus.getSaved();
        this.flagged = reportStatus.getFlagged();
        ReportMapper reportMapper = new ReportMapper();
        ClientUserMapper userMapper = new ClientUserMapper();
        this.reporter = userMapper.userToUserDTO(reportStatus.getReporter(), false);
        this.report = reportMapper.reportToReportDTO(reportStatus.getReport());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportStatusState getSaved() {
        return saved;
    }

    public void setSaved(ReportStatusState saved) {
        this.saved = saved;
    }

    public ReportStatusState getFlagged() {
        return flagged;
    }

    public void setFlagged(ReportStatusState flagged) {
        this.flagged = flagged;
    }

    public UserDTO getReporter() {
        return reporter;
    }

    public void setReporter(UserDTO reporter) {
        this.reporter = reporter;
    }

    public ReportDTO getReport() {
        return report;
    }

    public void setReport(ReportDTO report) {
        this.report = report;
    }
}
