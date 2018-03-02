package io.jenkins.plugins.kinesislogs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class KinesisWriter {
    private String streamName;
    private transient AmazonKinesisAsync kc;
    private final OutputStream errorStream;
    private final Run<?, ?> build;
    private final TaskListener listener;
    private Charset charset;

    private static final Logger LOGGER = Logger.getLogger(KinesisWriter.class.getName());

    public KinesisWriter(Run<? ,?> run, OutputStream error, TaskListener listener, Charset charset) {
        this.errorStream = error != null ? error : System.err;
        this.build = run;
        this.listener = listener;
        this.charset = charset;
        this.kc = new KinesisClientHelper().createClient();
        this.streamName = KinesisConfiguration.get().getStreamName();
        LOGGER.info("streamName: " + this.streamName);
    }

    public Charset getCharset() {
        return charset;
    }

    public void write(String line) {
        if (StringUtils.isNotEmpty(line)) {
//            LOGGER.info("Inside write method, writing");
            KinesisLogEvent event = new KinesisLogEvent(this.build, line);
            byte[] bytes = event.toJsonAsBytes();
            if (bytes == null) {
                LOGGER.warning("Could not get JSON bytes for event");
            }

            PutRecordRequest prr = new PutRecordRequest();
            prr.setStreamName(this.streamName);
            prr.setPartitionKey(event.getJobName());
            prr.setData(ByteBuffer.wrap(bytes));
            try {
                this.kc.putRecordAsync(prr);
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
