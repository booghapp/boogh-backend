package org.boogh.web.rest;


import com.codahale.metrics.annotation.Timed;
import org.boogh.config.ApplicationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for viewing the current environment configuration.
 */
@RestController
@RequestMapping("/api")
public class EnvironmentConfigResource {

    private final ApplicationProperties applicationProperties;

    public EnvironmentConfigResource(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * GET  /configuration : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/configuration")
    @Timed
    public List<String> getConfiguration() {
        List<String> properties = new ArrayList<>();
        properties.add(applicationProperties.getGoogle().getMapsApiKey());
        properties.add(applicationProperties.getAws().getBackendS3Location());
        return properties;
    }

}
