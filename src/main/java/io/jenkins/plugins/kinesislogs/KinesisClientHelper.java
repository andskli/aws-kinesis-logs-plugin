package io.jenkins.plugins.kinesislogs;

import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.*;

import java.util.logging.Logger;

public class KinesisClientHelper {
    private static final Logger LOGGER = Logger.getLogger(KinesisClientHelper.class.getName());

    public AmazonKinesisAsync createClient() {
        return AmazonKinesisAsyncClientBuilder.defaultClient();
    }
}
