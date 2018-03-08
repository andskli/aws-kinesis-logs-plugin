package io.jenkins.plugins.kinesislogs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.google.common.base.Strings;

import java.util.logging.Logger;

public class KinesisClientHelper {
    private static final Logger LOGGER = Logger.getLogger(KinesisClientHelper.class.getName());

    public AmazonKinesisAsync getAwsKinesis(final KinesisConfiguration config) {
        // If we find access keys, construct our own client
        if (!Strings.isNullOrEmpty(config.getAwsAccessKeyId()) && (!Strings.isNullOrEmpty(config.getAwsSecretKey()))) {
            AWSCredentials credentials = new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return config.getAwsAccessKeyId();
                }

                @Override
                public String getAWSSecretKey() {
                    return config.getAwsSecretKey();
                }
            };

            return AmazonKinesisAsyncClientBuilder.
                    standard().
                    withRegion(config.getAwsRegion()).
                    withCredentials(new AWSStaticCredentialsProvider(credentials)).
                    build();
        }
        // Otherwise, work through the default AWS SDK credentials chain
        return AmazonKinesisAsyncClientBuilder.standard().build();
    }
}
