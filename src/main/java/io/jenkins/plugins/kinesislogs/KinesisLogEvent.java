package io.jenkins.plugins.kinesislogs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.Run;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.Logger;

public final class KinesisLogEvent {

    private static final Logger LOGGER = Logger.getLogger(KinesisLogEvent.class.getName());


    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String jobName;
    private int buildNumber;
    private String message;
    private String jobUrl;
    private String timestamp; // UTC Timestamp
    private boolean isBuilding;

    public KinesisLogEvent(Run<?, ?> build, String message) {
        this.buildNumber = build.getNumber();
        this.jobName = build.getParent().getName();
        this.jobUrl = Jenkins.getInstance().getRootUrl() + build.getUrl();
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.isBuilding = build.isBuilding();
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getJobName() {
        return jobName;
    }

    public String getMessage() {
        return message;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isBuilding() {
        return isBuilding;
    }

    public byte[] toJsonAsBytes() {
        try {
            // LOGGER.info("EVENT: " + this.toString()); // Debug!
            return JSON.writeValueAsBytes(this);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            return JSON.writeValueAsString(this);
        } catch (IOException e) {
            return null;
        }
    }
}
