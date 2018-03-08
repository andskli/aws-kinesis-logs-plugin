package io.jenkins.plugins.kinesislogs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class KinesisWriter {
    private String streamName;
    private transient AmazonKinesisAsync kc;
    private final OutputStream errorStream;
    private final Run<?, ?> build;
    private final TaskListener listener;
    private Charset charset;
    private String partitionKey;

    private static final Logger LOGGER = Logger.getLogger(KinesisWriter.class.getName());

    public KinesisWriter(Run<? ,?> run, OutputStream error, TaskListener listener, Charset charset) {
        this.errorStream = error != null ? error : System.err;
        this.build = run;
        this.listener = listener;
        this.charset = charset;
        this.kc = new KinesisClientHelper().getAwsKinesis(KinesisConfiguration.get());
        this.streamName = KinesisConfiguration.get().getStreamName();

        /**
         * Use the MD5 sum of the JobName string as partitionKey, so that we don't
         * exceed the maximum char len of the Kinesis API
         */
        byte[] jobNameBytes = new byte[0];
        try {
            jobNameBytes = run.getParent().getName().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest(jobNameBytes);
        this.partitionKey = digest.toString();
    }

    public Charset getCharset() {
        return charset;
    }

    public void write(String line) {
        if (StringUtils.isNotEmpty(line)) {
            KinesisLogEvent event = new KinesisLogEvent(this.build, line);
            byte[] bytes = event.toJsonAsBytes();
            if (bytes == null) {
                LOGGER.warning("Could not get JSON bytes for event");
                return;
            }

            PutRecordRequest prr = new PutRecordRequest();
            prr.setStreamName(this.streamName);
            prr.setPartitionKey(this.partitionKey);
            prr.setData(ByteBuffer.wrap(bytes));
            try {
                this.kc.putRecordAsync(prr,
                        new AsyncHandler<PutRecordRequest, PutRecordResult>() {
                            @Override
                            public void onError(Exception exception) {
                                exception.printStackTrace();
                            }

                            @Override
                            public void onSuccess(PutRecordRequest request, PutRecordResult putRecordResult) {
                                // Do nothing.
                            }
                        });
            } catch (AmazonClientException ex) {
                LOGGER.warning("Error sending record to Amazon Kinesis:" + ex.toString());
            }
        }
    }

    public void close() {
        this.kc.shutdown();
    }

    private void logErrorMessage(String msg) {
        try {
            errorStream.write(msg.getBytes(charset));
            errorStream.flush();
        } catch (IOException ex) {
            // This should never happen, but if it does we just have to let it go.
            ex.printStackTrace();
        }
    }
}
