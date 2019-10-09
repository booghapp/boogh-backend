package org.boogh.clientservice.dto;

import org.boogh.config.Constants;
import org.boogh.domain.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private Long id;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user, boolean showFullProfile) {
        this.id = user.getId();
        this.login = user.getLogin();
        if (showFullProfile) {
            this.email = user.getEmail();
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", email='" + email + '\'' +
            "}";
    }
}
