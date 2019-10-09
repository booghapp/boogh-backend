package org.boogh.clientservice.dto;

import org.boogh.domain.Report;

public class SlimReportDTO {

    private Long id;

    public SlimReportDTO(Report report){
        this.id = report.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long reportId) {
        this.id = reportId;
    }
}
