package org.boogh.clientapi;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.clientservice.CheckAuth;
import org.boogh.config.Constants;
import org.boogh.domain.User;
import org.boogh.repository.UserRepository;
import org.boogh.service.MailService;
import org.boogh.service.UserService;
import org.boogh.service.dto.UserDTO;
import org.boogh.service.mapper.UserMapper;
import org.boogh.web.rest.UserResource;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.errors.EmailAlreadyUsedException;
import org.boogh.web.rest.errors.LoginAlreadyUsedException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientUserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    private final UserRepository userRepository;

    private final MailService mailService;

    private final CheckAuth checkAuth;

    private final UserMapper userMapper;

    public ClientUserResource(UserService userService, UserRepository userRepository, MailService mailService, CheckAuth checkAuth, UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.checkAuth = checkAuth;
        this.userMapper = userMapper;
    }

    /**
     * PUT /users : Updates an existing User.
     *
     * @param userDTO the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already in use
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already in use
     */
    @PutMapping("/users")
    @Timed
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);

        Long userId = userDTO.getId();
        if(!checkAuth.hasAuthority(userId)){
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        User currentUser = userRepository.findOneWithAuthoritiesById(userId).get();
        UserDTO currentUserDTO = userMapper.userToUserDTO(currentUser);
        if (userDTO.getLogin() != null) {
            currentUserDTO.setLogin(userDTO.getLogin());
        }

        Optional<UserDTO> updatedUser = userService.updateUser(currentUserDTO);

        return ResponseUtil.wrapOrNotFound(updatedUser,
            HeaderUtil.createAlert("A user is updated with identifier " + userDTO.getLogin(), userDTO.getLogin()));
    }
}
