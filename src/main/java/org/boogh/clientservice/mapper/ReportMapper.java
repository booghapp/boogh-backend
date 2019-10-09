package org.boogh.clientservice.mapper;

import org.boogh.clientservice.dto.ReportDTO;
import org.boogh.domain.Report;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReportMapper {

    public ReportDTO reportToReportDTO(Report report) {
        return new ReportDTO(report);
    }

    public List<ReportDTO> reportToReportDTOs(List<Report> reports) {
        return reports.stream()
            .filter(Objects::nonNull)
            .map(this::reportToReportDTO)
            .collect(Collectors.toList());
    }

    public Report reportDTOToReport(ReportDTO reportDTO) {
        if (reportDTO == null) {
            return null;
        } else {
            Report report = new Report();
            report.setId(reportDTO.getId());
            report.setType(reportDTO.getType());
            report.setAnonymous(reportDTO.getAnonymous());
            ClientUserMapper userMapper = new ClientUserMapper();
            report.setReporter(userMapper.userDTOToUser(reportDTO.getReporter()));
            report.setDescription(reportDTO.getDescription());
            report.setParent(this.reportDTOToReport(reportDTO.getParent()));
//            report.setLocation(reportDTO.getLocation());
            report.setComments(reportDTO.getComments());
            return report;
        }
    }

    public List<Report> reportDTOsToReports(List<ReportDTO> reportDTOs){
        return reportDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::reportDTOToReport)
            .collect(Collectors.toList());
    }
}
