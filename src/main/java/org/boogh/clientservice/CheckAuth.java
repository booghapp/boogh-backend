package org.boogh.clientservice;

import org.boogh.domain.User;
import org.boogh.repository.UserRepository;
import org.boogh.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckAuth {

    private static final Logger log = LoggerFactory.getLogger(CheckAuth.class);

    @Autowired
    private UserRepository userRepository;

    public boolean hasAuthority(Long id){
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if(login.get().equals("anonymousUser")){
            return false;
        }
        Optional<User> user = userRepository.findOneByLogin(login.get());
        Long currUserId = user.get().getId();
        if(!id.equals(currUserId)){
            log.error("Invalid credentials");
            return false;
        }
        return true;
    }
}
