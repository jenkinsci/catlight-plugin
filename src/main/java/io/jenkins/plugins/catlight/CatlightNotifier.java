
package io.jenkins.plugins.catlight;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Sends notification to CatLight service when jobs start and complete
 */
@Extension
public class CatlightNotifier extends RunListener<Run<?, ?>> {
    private static final Logger logger = Logger.getLogger(CatlightNotifier.class.getName());
    private final BlockingQueue<String> changedJobUrisQueue = new LinkedBlockingQueue<>();
    private final Thread notificationProcessorThread;
    private final CatlightServiceClient serviceClient;

    public CatlightNotifier() {
        this(new CatlightServiceClientImpl());
    }

    public CatlightNotifier(CatlightServiceClient serviceClient) {
        logger.info("Initializing CatLight notification plugin");
        this.serviceClient = serviceClient;
        notificationProcessorThread = new Thread(this::processNotificationQueue);
        notificationProcessorThread.start();
    }

    @Override
    public void onCompleted(Run<?, ?> run, TaskListener listener) {
        var jobUri = getJobUri(run);
        logger.log(Level.FINER, "Job completed: {0}", jobUri);
        changedJobUrisQueue.add(jobUri);
    }

    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        var jobUri = getJobUri(run);
        logger.log(Level.FINER, "Job started: {0}", jobUri);
        changedJobUrisQueue.add(jobUri);
    }

    private void processNotificationQueue() {
        try {
            while (true) {

                try {
                    var jobUri = changedJobUrisQueue.take();

                    CatlightConfiguration catlightConfiguration = CatlightConfiguration.get();

                    if (!catlightConfiguration.hasTeamIds()) {
                        logger.log(Level.FINE, "CatLight plugin is not configured. " +
                                "Please set team ids in global settings");
                    } else {
                        serviceClient.notifyAboutEntityChange(catlightConfiguration.getTeamIds(), jobUri);
                    }

                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception ex) {
                    logger.log(Level.WARNING,
                            "Cannot notify CatLight about job status change", ex);
                    //noinspection BusyWait
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            logger.log(Level.FINE, "Interrupted", e);
        }
    }

    private static String getJobUri(Run<?, ?> run) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(Utils.getDefaultSpaceUri());
        uriBuilder.append(":bd:");

        var fullJobName = run.getParent().getFullName();
        var encodedJobName = Utils.encodeURIComponent(fullJobName);
        uriBuilder.append(encodedJobName);

        return uriBuilder.toString();
    }
}
