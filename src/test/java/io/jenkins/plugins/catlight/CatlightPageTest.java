package io.jenkins.plugins.catlight;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsSessionRule;

import static org.junit.Assert.*;

public class CatlightPageTest {
    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    @Test
    public void canLoadCatlightPageUi() throws Throwable {
        sessions.then(r -> {
            var page = r.createWebClient().goTo("catlight");
            assertNotNull(page.getElementById("downloadButton"));
        });
    }

}
