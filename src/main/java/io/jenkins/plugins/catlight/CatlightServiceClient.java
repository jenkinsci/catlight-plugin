package io.jenkins.plugins.catlight;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * CatLight service client api
 */
public interface CatlightServiceClient {

    /**
     * Notify the service about changed job status
     * @param teamIds comma-separated list of team guids
     * @param jobUri job uri
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    void notifyAboutEntityChange(String teamIds, String jobUri)
            throws URISyntaxException, IOException, InterruptedException;


    /**
     * Configure CatLight service to wait for notification instead of using polling
     * @param spaceUri CatLight space uri
     * @param teamIds comma-separated list of team guids
     * @param notificationsEnabled enable or disable notification processing
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    void setAcceleratedNotifications(String spaceUri, String teamIds, Boolean notificationsEnabled)
            throws URISyntaxException, IOException, InterruptedException;
}
