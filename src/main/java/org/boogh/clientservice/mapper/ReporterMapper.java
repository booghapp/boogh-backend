package org.boogh.clientservice.mapper;

import org.boogh.clientservice.dto.ReporterDTO;
import org.boogh.domain.Reporter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReporterMapper {
    public ReporterDTO reporterToReporterDTO(Reporter reporter, boolean showFullProfile) {
        return new ReporterDTO(reporter, showFullProfile);
    }

    /*public List<ReporterDTO> reportersToReporterDTOs(List<Reporter> reporter) {
        return reporter.stream()
            .filter(Objects::nonNull)
            .map(this::reporterToReporterDTO)
            .collect(Collectors.toList());
    }*/

    public Reporter reporterDTOToReporter(ReporterDTO reporterDTO) {
        if (reporterDTO == null) {
            return null;
        } else {
            Reporter reporter = new Reporter();
            ClientUserMapper userMapper = new ClientUserMapper();
            reporter.setUser(userMapper.userDTOToUser(reporterDTO.getUser()));
            reporter.setAbout(reporterDTO.getAbout());
            reporter.setKarma(reporterDTO.getKarma());
            reporter.setLocation(reporterDTO.getLocation());
            reporter.setId(reporter.getUser().getId());
            return reporter;
        }
    }

    public List<Reporter> reporterDTOsToReporters(List<ReporterDTO> reporterDTOs) {
        return reporterDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::reporterDTOToReporter)
            .collect(Collectors.toList());
    }
}
