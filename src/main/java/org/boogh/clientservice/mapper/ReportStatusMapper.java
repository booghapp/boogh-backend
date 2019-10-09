package org.boogh.clientservice.mapper;

import org.boogh.clientservice.dto.ReportStatusDTO;
import org.boogh.domain.ReportStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReportStatusMapper {


    public ReportStatusDTO reportStatusToReportStatusDTO(ReportStatus reportStatus) {
        return new ReportStatusDTO(reportStatus);
    }

    public List<ReportStatusDTO> reportStatusesToReportStatusDTOs(List<ReportStatus> reportStatuses) {
        return reportStatuses.stream()
            .filter(Objects::nonNull)
            .map(this::reportStatusToReportStatusDTO)
            .collect(Collectors.toList());
    }

    public ReportStatus reportStatusDTOToReportStatus(ReportStatusDTO reportStatusDTO) {
        if (reportStatusDTO == null) {
            return null;
        } else {
            ReportStatus reportStatus = new ReportStatus();
            reportStatus.setId(reportStatusDTO.getId());
            return reportStatus;
        }
    }

    public List<ReportStatus> reportStatusDTOsToReportsStatuses(List<ReportStatusDTO> reportStatusDTOs){
        return reportStatusDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::reportStatusDTOToReportStatus)
            .collect(Collectors.toList());
    }
}
