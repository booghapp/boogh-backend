package org.boogh.clientservice.dto;

import org.boogh.clientservice.mapper.ClientUserMapper;
import org.boogh.domain.Honk;

public class HonkDTO {

    private Long id;

    private Boolean honked;

    private UserDTO user;

    private SlimReportDTO report;

    public HonkDTO(Honk honk) {
        this.id = honk.getId();
        this.honked = honk.isHonked();
        ClientUserMapper userMapper = new ClientUserMapper();
        this.user = userMapper.userToUserDTO(honk.getUser(), false);
        this.report = new SlimReportDTO(honk.getReport());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getHonked() {
        return honked;
    }

    public void setHonked(Boolean honked) {
        this.honked = honked;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public SlimReportDTO getReport() {
        return report;
    }

    public void setReport(SlimReportDTO report) {
        this.report = report;
    }
}
