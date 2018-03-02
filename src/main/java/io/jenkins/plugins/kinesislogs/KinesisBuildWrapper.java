package io.jenkins.plugins.kinesislogs;

import com.amazonaws.services.kinesis.AmazonKinesis;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public final class KinesisBuildWrapper extends BuildWrapper {
    private static final Logger LOGGER = Logger.getLogger(KinesisBuildWrapper.class.getName());

    @DataBoundConstructor
    public KinesisBuildWrapper(String streamName) {}

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        return new Environment() {};
    }

    KinesisWriter getKinesisWriter(AbstractBuild<?, ?> build, OutputStream errorStream) {


        return new KinesisWriter(build, errorStream, null, build.getCharset());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream decorateLogger(AbstractBuild build, OutputStream logger) {
        KinesisWriter kinesis = getKinesisWriter(build, logger);

        return new KinesisOutputStream(logger, kinesis);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(KinesisBuildWrapper.class);
            load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            return true;
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return String.format("Kinesis Build Wrapper");
        }
    }
}
