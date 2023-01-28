package io.catlight.plugins.jenkins;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsSessionRule;

public class CatlightConfigurationTest {
    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    @Test
    public void canSaveTeamSettings() throws Throwable {
        sessions.then(r -> {
            assertNull("team id not set initially", CatlightConfiguration.get().getTeamIds());
            assertFalse("don't have team id initially", CatlightConfiguration.get().hasTeamIds());

            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
            HtmlTextInput textbox = config.getInputByName("_.teamIds");
            textbox.setText("999d442a-823e-45aa-8833-a2c53023d099");
            r.submit(config);
            assertEquals("team id was set from config page", "999d442a-823e-45aa-8833-a2c53023d099",
                    CatlightConfiguration.get().getTeamIds());
        });
        sessions.then(r -> {
            assertEquals("team id is set after restart of Jenkins",
                    "999d442a-823e-45aa-8833-a2c53023d099",
                    CatlightConfiguration.get().getTeamIds());
            assertTrue("has team id after restart", CatlightConfiguration.get().hasTeamIds());
        });
    }

}
