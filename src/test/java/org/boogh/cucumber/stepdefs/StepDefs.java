package org.boogh.cucumber.stepdefs;

import org.boogh.BooghApp;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = BooghApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
