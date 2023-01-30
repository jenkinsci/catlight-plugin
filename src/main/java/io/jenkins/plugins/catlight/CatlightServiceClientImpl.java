package io.jenkins.plugins.catlight;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of CatLight service client
 */
public class CatlightServiceClientImpl implements CatlightServiceClient {

    private static final Logger logger = Logger.getLogger(CatlightServiceClientImpl.class.getName());
    private final HttpClient httpClient;
    private final String pluginVersion;

    private final String serviceBaseUrl = "https://notify.catlight.io/";

    public CatlightServiceClientImpl() {

        Jenkins jenkins = Jenkins.getInstanceOrNull();
        ProxyConfiguration proxy = jenkins != null ? jenkins.proxy : null;

        var builder = HttpClient.newBuilder();
        // TODO: set proxy

        httpClient = builder.build();

        pluginVersion = this.getClass().getPackage().getSpecificationVersion();
    }

    @Override
    public void notifyAboutEntityChange(String teamIds, String jobUri)
            throws URISyntaxException, IOException, InterruptedException {
        var url = serviceBaseUrl + "v1/generic?teamIds=" + teamIds + "&uri=" + jobUri;

        HttpRequest request = createRequest()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccessResponse(response);
    }

    @Override
    public void setAcceleratedNotifications(String spaceUri, String teamIds, Boolean notificationsEnabled)
            throws URISyntaxException, IOException, InterruptedException {
        var url = serviceBaseUrl + "v1/space/SetEnabledTypes?teamIds=" + teamIds
                + "&uri=" + spaceUri
                + "&types=";

        if (notificationsEnabled) {
            url += "BuildDefinition,ReleaseDefinition";
        }

        HttpRequest request = createRequest()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccessResponse(response);
    }

    private void ensureSuccessResponse(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            logger.log(Level.WARNING, "Got unexpected response from CatLight server: {0} {1}",
                    new Object[]{response.statusCode(), response.body()});

            throw new RuntimeException("Got unexpected response from CatLight server. See logs for details. Code: " + response.statusCode());
        }
    }

    private HttpRequest.Builder createRequest() {
        return HttpRequest.newBuilder()
                .headers("User-Agent", "CatLight-Jenkins/" + pluginVersion);
    }
}
