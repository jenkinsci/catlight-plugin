package io.jenkins.plugins.catlight;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import jenkins.model.Jenkins;

import java.util.Random;

/**
 * CatLight Jenkins page with setup instructions
 */
@Extension
public class CatlightPage implements UnprotectedRootAction {
    private final Random random = new Random();

    public String getIconFileName() {
        return "/plugin/catlight/app-128.png";
    }

    public String getDisplayName() {
        return "CatLight";
    }

    public String getUrlName() {
        return "catlight";
    }

    public boolean getIsConfigured() {
        return CatlightConfiguration.get().hasTeamIds();
    }

    public String getConfigurationPageUrl() {
        return Jenkins.get().getRootUrl() + "manage/configure";
    }
}
