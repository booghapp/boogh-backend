package org.boogh.clientservice.mapper;

import org.boogh.clientservice.dto.UserDTO;
import org.boogh.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * mapper for the entity User and its DTO called UserDTO.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class ClientUserMapper {


    public UserDTO userToUserDTO(User user, boolean showFullProfile) {
        return new UserDTO(user, showFullProfile);
    }

    /*public List<UserDTO> usersToUserDTOs(List<User> users) {
        return users.stream()
            .filter(Objects::nonNull)
            .map(this::userToUserDTO)
            .collect(Collectors.toList());
    }*/

    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            User user = new User();
            user.setLogin(userDTO.getLogin());
            user.setEmail(userDTO.getEmail());
            return user;
        }
    }

    public List<User> userDTOsToUsers(List<UserDTO> userDTOs) {
        return userDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::userDTOToUser)
            .collect(Collectors.toList());
    }

    public User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
