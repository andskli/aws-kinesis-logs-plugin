package io.jenkins.plugins.kinesislogs;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class KinesisConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static KinesisConfiguration get() {
        return GlobalConfiguration.all().get(KinesisConfiguration.class);
    }

    private String streamName;
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String awsRegion;

    public KinesisConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public String getStreamName() {
        return streamName;
    }

    @Nonnull
    @DataBoundSetter
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    @DataBoundSetter
    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
        save();
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    @DataBoundSetter
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
        save();
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    @DataBoundSetter
    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
        save();
    }

    public FormValidation doCheckStreamName(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.error("You need to specify a stream name");
        }
        return FormValidation.ok();
    }

}
