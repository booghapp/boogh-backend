package org.boogh.clientservice.dto;

import org.boogh.clientservice.mapper.ClientUserMapper;
import org.boogh.domain.Reporter;

import javax.persistence.Lob;

public class ReporterDTO {

    private Long id;

    @Lob
    private String about;

    private Integer karma;

    private String location;

    private Boolean notificationsOn;

    private UserDTO user;

    public ReporterDTO(Reporter reporter, boolean showFullProfile){
        this.id = reporter.getId();
        this.about = reporter.getAbout();
        this.karma = reporter.getKarma();
        this.location = reporter.getLocation();
        this.notificationsOn = reporter.isNotificationsOn();
        ClientUserMapper userMapper = new ClientUserMapper();
        this.user = userMapper.userToUserDTO(reporter.getUser(), showFullProfile);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getNotificationsOn() {
        return notificationsOn;
    }

    public void setNotificationsOn(Boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

}
