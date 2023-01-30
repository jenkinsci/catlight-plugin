package io.jenkins.plugins.catlight;

import hudson.Extension;
import hudson.ExtensionList;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Catlight plugin configuration
 */
@Extension
public class CatlightConfiguration extends GlobalConfiguration {

    private static final Logger logger = Logger.getLogger(CatlightConfiguration.class.getName());

    private final transient CatlightServiceClient serviceClient;

    /**
     * @return the singleton instance
     */
    public static CatlightConfiguration get() {
        return ExtensionList.lookupSingleton(CatlightConfiguration.class);
    }

    private String teamIds;

    public CatlightConfiguration() {
        this(new CatlightServiceClientImpl());
    }

    public CatlightConfiguration(CatlightServiceClient serviceClient) {
        this.serviceClient = serviceClient;

        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /**
     * @return Coma-separated list of CatLight team ids that will receive notifications from this server
     */
    public String getTeamIds() {
        return teamIds;
    }

    /**
     * @return True, if team id has been configured
     */
    public boolean hasTeamIds() {
        return teamIds != null && !teamIds.isBlank();
    }

    @DataBoundSetter
    public void setTeamIds(String teamIds) {
        if (Objects.equals(teamIds, this.teamIds)) {
            return;
        }

        String spaceUri = Utils.getDefaultSpaceUri();

        try {
            if (this.hasTeamIds()) {
                logger.log(Level.INFO, String.format(
                        "Clearing previous notification settings for teams: '%s'. Space uri: '%s'",
                        teamIds, spaceUri));

                // removing old notification settings
                serviceClient.setAcceleratedNotifications(spaceUri, this.teamIds, false);
            }

            logger.log(Level.INFO, String.format("Configuring notification settings for teams: '%s'. Space uri: '%s'",
                    teamIds, spaceUri));


            if (teamIds != null && !teamIds.isBlank()) {
                serviceClient.setAcceleratedNotifications(spaceUri, teamIds.trim(), true);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.teamIds = teamIds.trim();
        save();
    }
}
