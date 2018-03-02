package io.jenkins.plugins.kinesislogs;

import hudson.console.LineTransformationOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class KinesisOutputStream extends LineTransformationOutputStream {
    private final OutputStream delegate;
    private final KinesisWriter kinesis;

    public KinesisOutputStream(OutputStream delegate, KinesisWriter kinesis) {
        super();
        this.delegate = delegate;
        this.kinesis = kinesis;
    }

    @Override
    protected void eol(byte[] b, int len) throws IOException {
        delegate.write(b, 0, len);
        this.flush();

        String line = new String(b, 0, len, "UTF-8").trim();
        kinesis.write(line);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
        super.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
        kinesis.close();
        super.flush();
    }
}
